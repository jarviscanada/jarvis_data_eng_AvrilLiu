"use strict";

/**
 * DRY: shared counter helper
 */
function incrementCount(map, key) {
  if (!key) return;
  map[key] = (map[key] || 0) + 1;
}

/**
 * TEXT: count line number based on '\n'
 */
function countLines(text) {
  if (text.length === 0) return 0;
  return text.split("\n").length;
}

/**
 * Remove <!DOCTYPE html> and HTML comments so they won't be detected as tags.
 */
function sanitizeHtmlNoise(text) {
  const noDoctype = text.replace(/<!doctype\s+html\s*>/gi, "");
  const noComments = noDoctype.replace(/<!--[\s\S]*?-->/g, "");
  return noComments;
}

/**
 * Detect HTML by finding at least one tag (after sanitizing).
 */
function looksLikeHtml(text) {
  const cleaned = sanitizeHtmlNoise(text);
  const tagRegex = /<\/?\s*([a-z][a-z0-9-]*)\b[^>]*>/i;
  return tagRegex.test(cleaned);
}

/**
 * Count HTML opening tags only (so <div></div> counts as div:1).
 * DOCTYPE and comments have been removed.
 */
function countHtmlTags(text) {
  const tags = {};
  const cleaned = sanitizeHtmlNoise(text);

  // opening tag only: starts with "<" not "</"
  const tagRegexGlobal = /<\s*([a-z][a-z0-9-]*)\b[^>]*>/gi;

  let match;
  while ((match = tagRegexGlobal.exec(cleaned)) !== null) {
    const tagName = match[1].toLowerCase();
    incrementCount(tags, tagName);
  }
  return tags;
}

/**
 * Detect CSS by finding blocks like: selector { ... }
 * (Simple heuristic, good enough for this exercise.)
 */
function looksLikeCss(text) {
  const cssBlockRegex = /[^{}]+\{[^{}]*\}/;
  return cssBlockRegex.test(text);
}

/**
 * Count CSS targets (selectors) by reading the part before "{...}".
 * Supports comma-separated selectors: "div, a { ... }"
 */
function countCssTargets(text) {
  const cssTargets = {};
  const blockRegexGlobal = /([^{}]+)\{[^{}]*\}/g;

  let match;
  while ((match = blockRegexGlobal.exec(text)) !== null) {
    const selectorPart = match[1].trim();
    if (!selectorPart) continue;

    const selectors = selectorPart
    .split(",")
    .map((s) => s.trim())
    .filter(Boolean);

    for (const sel of selectors) {
      incrementCount(cssTargets, sel);
    }
  }
  return cssTargets;
}

/**
 * OO wrapper: decides type and returns correct output shape.
 */
class ContentAnalyzer {
  constructor(input) {
    this.content = String(input);
  }

  analyze() {
    // HTML first (HTML might contain "style" or braces etc.)
    if (looksLikeHtml(this.content)) {
      return {
        contentType: "HTML",
        tags: countHtmlTags(this.content),
      };
    }

    if (looksLikeCss(this.content)) {
      return {
        contentType: "CSS",
        cssTargets: countCssTargets(this.content),
      };
    }

    return {
      contentType: "TEXT",
      lineNumber: countLines(this.content),
    };
  }
}

/**
 * Required function
 */
function analyzeContent(input) {
  return new ContentAnalyzer(input).analyze();
}

/* ----------------- quick tests ----------------- */
if (require.main === module) {
  console.log(analyzeContent("this is a test\nSeems to work"));
  console.log(analyzeContent("body{blabla} a{color:#fff} a{ padding:0}"));
  console.log(analyzeContent("<html><div></div><div></div></html>"));
  console.log(analyzeContent("<!DOCTYPE html><!--x--><div></div>"));
  console.log(analyzeContent("div, a { padding:0 } a{color:red}"));
}

module.exports = { analyzeContent };

