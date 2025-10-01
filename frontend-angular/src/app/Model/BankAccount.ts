export class BankAccount {
  id!: string;
  userId!: string;
  balance!: number;
  accountType!: string;
  pendingTransactions!: number;

  constructor(id: string, userId: string, balance: number, accountType: string) {
    this.id = id;
    this.userId = userId;
    this.balance = balance;
    this.accountType = accountType;
    this.pendingTransactions = 0;
  }

}
