import assert from "node:assert/strict";
import { LinkedList } from "./linked-list";

function run() {
  {
    const list = new LinkedList<number>();
    list.push(1);
    list.push(2);
    list.push(3);
    assert.equal(list.length, 3);
    assert.deepEqual(list.toArray(), [1, 2, 3]);
    assert.equal(list.head?.value, 1);
    assert.equal(list.tail?.value, 3);
  }

  {
    const list = new LinkedList<number>();
    list.unshift(2);
    list.unshift(1);
    list.unshift(0);
    assert.equal(list.length, 3);
    assert.deepEqual(list.toArray(), [0, 1, 2]);
    assert.equal(list.head?.value, 0);
    assert.equal(list.tail?.value, 2);
  }

  {
    const list = new LinkedList<number>();
    list.push(1);
    list.push(2);
    list.push(3);

    const v = list.pop();
    assert.equal(v, 3);
    assert.equal(list.length, 2);
    assert.deepEqual(list.toArray(), [1, 2]);
    assert.equal(list.tail?.value, 2);
    assert.equal(list.tail?.next, null);
  }

  {
    const list = new LinkedList<number>();
    list.push(1);
    list.push(2);
    list.push(3);

    const v = list.shift();
    assert.equal(v, 1);
    assert.equal(list.length, 2);
    assert.deepEqual(list.toArray(), [2, 3]);
    assert.equal(list.head?.value, 2);
    assert.equal(list.head?.prev, null);
  }

  {
    const list = new LinkedList<number>();
    list.push(10);
    assert.equal(list.pop(), 10);
    assert.equal(list.length, 0);
    assert.equal(list.head, null);
    assert.equal(list.tail, null);
  }

  {
    const list = new LinkedList<number>();
    list.unshift(10);
    assert.equal(list.shift(), 10);
    assert.equal(list.length, 0);
    assert.equal(list.head, null);
    assert.equal(list.tail, null);
  }

  console.log("All tests passed.");
}

run();
