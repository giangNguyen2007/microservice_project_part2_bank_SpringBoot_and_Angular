export class BankTransaction {
  // create a bank transaction class with the following properties:
  id!: string;
  accountId!: string;
  description!: string;
  amount!: number;
  status!: TransactionStatus;
  createdAt!: Date;  // pour recevoir la date du backend en string
  isDebit!: boolean; // true if amount < 0, false otherwise

  constructor(public _id: string, public _accountId: string, public accountType: string, public _description: string, amount: number, status : TransactionStatus, _isDebit: boolean) {
    this.id = _id;
    this.accountId = _accountId;
    this.description = _description;
    this.amount = amount;
    this.status = status;
    this.isDebit = _isDebit;
  }

}

export enum TransactionStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED'
}


