
// Transaction submitted by ecommerce app
// to be validated by user before being registered as a bank transaction
export class IncomingTransaction {
  // create a bank transaction class with the following properties:
  id!: string;
  accountId!: string;
  description!: string;
  amount!: number;
  createdAt!: Date;
  validated!: boolean;

  constructor( public _id: string, public _accountId: string, public accountType: string, public _description: string, amount: number, validated : boolean) {

    this.id = _id;
    this.accountId = _accountId;
    this.description = _description;
    this.amount = amount;
    this.validated = validated;

  }

}
