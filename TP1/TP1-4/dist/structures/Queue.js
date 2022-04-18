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
    deleteMessage(messageUuid) {
        const index = this.items.findIndex(message => message.uuid === messageUuid);
        if (index < 0)
            return false;
        this.items.splice(index, 1);
        return true;
    }
}
exports.default = Queue;
