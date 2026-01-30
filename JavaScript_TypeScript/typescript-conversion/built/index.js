"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const analyzeContent_1 = require("./analyzeContent");
console.log((0, analyzeContent_1.analyzeContent)("this is a test\nSeems to work"));
// ➞ { contentType: 'TEXT', lineNumber: 2 }
console.log((0, analyzeContent_1.analyzeContent)("body{blabla} a{color:#fff} a{ padding:0}"));
// ➞ { contentType: 'CSS', cssTargets: { body: 1, a: 2 } }
console.log((0, analyzeContent_1.analyzeContent)("<!DOCTYPE html><!--x--><html><div></div><div></div></html>"));
// ➞ { contentType: 'HTML', tags: { html: 1, div: 2 } }
