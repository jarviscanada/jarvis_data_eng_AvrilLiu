import { analyzeContent } from "./analyzeContent";

console.log(analyzeContent("this is a test\nSeems to work"));
// ➞ { contentType: 'TEXT', lineNumber: 2 }

console.log(analyzeContent("body{blabla} a{color:#fff} a{ padding:0}"));
// ➞ { contentType: 'CSS', cssTargets: { body: 1, a: 2 } }

console.log(analyzeContent("<!DOCTYPE html><!--x--><html><div></div><div></div></html>"));
// ➞ { contentType: 'HTML', tags: { html: 1, div: 2 } }
