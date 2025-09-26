import {Component, computed, effect,  signal,  ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

import {CurrencyPipe, NgIf, SlicePipe} from '@angular/common';
import {AuthService} from '../services/auth.service';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {NewProdctDto, ProductService} from '../services/product.service';
import {Product} from '../model/Product';
import {OrderService} from '../services/orderService';


@Component({
  selector: 'account-page',
  imports: [
    ReactiveFormsModule,
    NgIf,

    CurrencyPipe,
  ],
  templateUrl: './product-list-page.component.html',
  styleUrl: './product-list-page.component.scss'
})
export class ProductListPage {


  newProductForm! : FormGroup;



  loginSuccess = computed( () => this.authService.getToken() != null ) ;

  bankAccounts = computed( () => this.productService.getProductList() ) ;

  isUserAdmin = computed( () => this.authService.getUsers()?.role === "admin" ) ;



  myEffect = effect(
    () => {

        if (this.loginSuccess()) {
          this.productService.fetchProducts();
        }
    }
  )

  urlRegex = /(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_+.~#?&/=]*)/;

  categoryOptions = [
    { value: 'PHONES', label: 'Phones' },
    { value: 'TABLETTES', label: 'Tablette' },
    { value: 'ORDINATEURS', label: 'Ordinateur' }
  ];

  constructor(private formBuilder: FormBuilder,
              private authService : AuthService,
              private productService: ProductService,
              private orderService : OrderService,
              private rt : Router) {
    this.initLoginForm(formBuilder);

  }


  initLoginForm(formBuilder: FormBuilder) : void {


    this.newProductForm = this.formBuilder.group({
        title: ['MabBook', [Validators.required, Validators.minLength(3)]],
        description: ['My MacBook', [Validators.required, Validators.minLength(10)]],
        photoUrl: ['https://www.backmarket.fr/cdn-cgi/image/format%3Dauto%2Cquality%3D75%2Cwidth%3D3840/https://d2e6ccujb3mkqf.cloudfront.net/7338ad2c-e097-4be9-b58a-cb2500bd3398-1_7ddca168-ad16-4eef-92f9-a202235c90e4.jpg', [Validators.required, Validators.pattern(this.urlRegex)]],
        price: [1000, [Validators.required, Validators.min(10)]],
        stock: [10, [Validators.required, Validators.min(1)]],
        category: ['', [Validators.required, Validators.minLength(3)]],

      },{
        updateOn: 'blur'  // emit value and validation only on blur event
      }

    );

  }



  createNewProduct() {
    // extract date from the form
    const title = this.newProductForm.get('title')?.value;
    const description = this.newProductForm.get('description')?.value;
    const photoUrl = this.newProductForm.get('photoUrl')?.value;
    const price = this.newProductForm.get('price')?.value;
    const stock = this.newProductForm.get('stock')?.value;
    const category = this.newProductForm.get('category')?.value;

    if (!title || !description || !photoUrl || !price || !stock || !category) {
      alert('All fields are required!');
      return;
    }

    const newProduct : NewProdctDto = {
      title,
      description,
      photoUrl,
      price,
      stock,
      category
    };

    this.productService.createProduct(newProduct).subscribe(
      {
        next: (response => {
          console.log('Product creation successful, product Id: ' + response.id);

          this.newProductForm.reset();
          alert("product creation successful, product Id: " + response.id);

          this.productService.fetchProducts();

        }),

        error: (error => {
          const errorResponse = error as HttpErrorResponse;
          console.log('Status: ' + (errorResponse.status ));

          console.log('Account creation failed, message : ' + (errorResponse.error ));
          alert("Account creation failed: " + (errorResponse.error ));

        })

      }
    )
  }



  onIncrementCartClick( product : Product) : void {

    console.log("onAddCartClick called with productId: " + product.id);

    if (!product) {
      console.error('Product is null or undefined');
      return;
    }
    //console.log('Add to cart clicked for productId: ' + productId);
    this.orderService.incrementQty(product);
    // this.transactionsSection.refreshTransactions();

  }

  onDecrementCartClick( product : Product) : void {

    if (!product) {
      console.error('Product is null or undefined');
      return;
    }
    this.orderService.decrementQty(product);

  }

  onTransactionCreated(event: { accoutId: string; pendingAmount: number }){
    console.log('Transaction created event received in AccountPage for accountId: ' + event.accoutId + ', pendingAmount: ' + event.pendingAmount);

    if (!this.bankAccounts()){
      return;
    }

    // use the non null assertion operator " ! " to tell typescript that bankAccounts is not null
    const currentAccount : Product[] = this.bankAccounts()!;
    const updatedAccounts = currentAccount?.map( account => {
      if ( account.id === event.accoutId) {
        return { ...account, price: account.price + event.pendingAmount } ;
      }
      return account;
    }) ;

    this.productService.updateProductList(updatedAccounts);


  }






}
