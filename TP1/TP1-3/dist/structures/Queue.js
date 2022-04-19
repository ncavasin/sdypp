"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
class Queue {
    constructor(items) {
        this.items = items || [];
    }
    get length() {
        return this.items.length;
    }
    isEmpty() {
        return this.items.length === 0;
    }
    head() {
        return this.items[0] || null;
    }
    push(message) {
        this.items.push(message);
    }
    ;
    pop() {
        return this.items.shift();
    }
    clear() {
        this.items = [];
    }
    clone() {
        return new Queue([...this.items]);
    }
}
exports.default = Queue;
