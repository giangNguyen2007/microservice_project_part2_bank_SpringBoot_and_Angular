export class BankTransaction {
  // create a bank transaction class with the following properties:
  id!: string;
  accountId!: string;
  description!: string;
  amount!: number;
  status!: TransactionStatus;
  createdAt!: Date;  // pour recevoir la date du backend en string

  constructor(public _id: string, public _accountId: string, public accountType: string, public _description: string, amount: number, status : TransactionStatus) {
    this.id = _id;
    this.accountId = _accountId;
    this.description = _description;
    this.amount = amount;
    this.status = status;
  }

}

export enum TransactionStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED'
}


