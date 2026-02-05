export class Node<T> {
  public value: T;
  public next: Node<T> | null = null;
  public prev: Node<T> | null = null;

  constructor(value: T) {
    this.value = value;
  }
}
