import { Node } from "./node";

export class LinkedList<T> {
  public head: Node<T> | null = null;
  public tail: Node<T> | null = null;
  public length = 0;

  private link(a: Node<T> | null, b: Node<T> | null): void {
    if (a) a.next = b;
    if (b) b.prev = a;
  }

  push(value: T): void {
    const node = new Node(value);

    if (!this.tail) {
      this.head = node;
      this.tail = node;
      this.length = 1;
      return;
    }

    this.link(this.tail, node);
    this.tail = node;
    this.length += 1;
  }

  pop(): T {
    if (!this.tail) {
      throw new Error("Cannot pop from an empty list");
    }

    const removed = this.tail;
    const prev = removed.prev;

    if (!prev) {
      this.head = null;
      this.tail = null;
      this.length = 0;
      return removed.value;
    }

    this.link(prev, null);
    this.tail = prev;
    removed.prev = null;
    this.length -= 1;

    return removed.value;
  }

  unshift(value: T): void {
    const node = new Node(value);

    if (!this.head) {
      this.head = node;
      this.tail = node;
      this.length = 1;
      return;
    }

    this.link(node, this.head);
    this.head = node;
    this.length += 1;
  }

  shift(): T {
    if (!this.head) {
      throw new Error("Cannot shift from an empty list");
    }

    const removed = this.head;
    const next = removed.next;

    if (!next) {
      this.head = null;
      this.tail = null;
      this.length = 0;
      return removed.value;
    }

    this.link(null, next);
    this.head = next;
    removed.next = null;
    this.length -= 1;

    return removed.value;
  }

  toArray(): T[] {
    const out: T[] = [];
    let cur = this.head;
    while (cur) {
      out.push(cur.value);
      cur = cur.next;
    }
    return out;
  }
}
