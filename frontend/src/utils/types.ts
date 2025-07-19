export class ApiError<T = any> extends Error {
  public readonly status: number;
  public readonly data?: T;

  constructor(status: number, message: string, data?: T) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}
