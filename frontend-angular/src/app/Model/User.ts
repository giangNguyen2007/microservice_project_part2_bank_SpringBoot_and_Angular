// create a user class with the following properties:
export class User {
  userId!: string;
  name!: string;
  email!: string;
  role!: "USER" | "ADMIN" | null;

  constructor(userId: string, userName: string, email: string, role: "USER" | "ADMIN" | null) {
    this.userId = userId;
    this.name = userName;
    this.email = email;
    this.role = role;

  }

}
