"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.analyzeContent = analyzeContent;
/**
 * Public API:
 * - Detects HTML, CSS, else TEXT
 * - HTML: returns { contentType: "HTML", tags: {...} }
 * - CSS: returns { contentType: "CSS", cssTargets: {...} }
 * - TEXT: returns { contentType: "TEXT", lineNumber: n }
 */
function analyzeContent(input) {
    const content = String(input);
    if (looksLikeHtml(content)) {
        return { contentType: "HTML", tags: countHtmlTags(content) };
    }
    if (looksLikeCss(content)) {
        return { contentType: "CSS", cssTargets: countCssTargets(content) };
    }
    return { contentType: "TEXT", lineNumber: countLines(content) };
}
/** TEXT helpers */
function countLines(text) {
    if (text.length === 0)
        return 0;
    return text.split("\n").length;
}
/** HTML helpers */
function sanitizeHtmlNoise(text) {
    // Remove <!DOCTYPE html> (case-insensitive)
    const noDoctype = text.replace(/<!doctype\s+html\s*>/gi, "");
    // Remove HTML comments <!-- ... -->
    const noComments = noDoctype.replace(/<!--[\s\S]*?-->/g, "");
    return noComments;
}
function countHtmlTags(text) {
    const tags = {};
    const cleaned = sanitizeHtmlNoise(text);
    /**
     * Match opening tags only, e.g. <div>, <a href="...">
     * - excludes closing tags </div>
     * - ignores doctype (removed) and comments (removed)
     */
    const openTagRegex = /<\s*([a-z][a-z0-9-]*)\b[^>]*>/gi;
    let match;
    while ((match = openTagRegex.exec(cleaned)) !== null) {
        const tagName = match[1].toLowerCase();
        incrementCount(tags, tagName);
    }
    return tags;
}
function looksLikeHtml(text) {
    const cleaned = sanitizeHtmlNoise(text);
    // a simple heuristic: has at least one opening tag like <div ...>
    return /<\s*[a-z][a-z0-9-]*\b[^>]*>/i.test(cleaned);
}
/** CSS helpers */
function sanitizeCssNoise(text) {
    // Remove CSS block comments /* ... */
    return text.replace(/\/\*[\s\S]*?\*\//g, "");
}
function countCssTargets(text) {
    var _a;
    const targets = {};
    const cleaned = sanitizeCssNoise(text);
    /**
     * Very practical selector extraction for this exercise:
     * - count selectors immediately before "{"
     * - support comma-separated selectors: "body, a { ... }"
     * - take the last simple token in each selector (good for "div a", ".btn:hover", "#id")
     */
    const blockRegex = /([^{}]+)\{/g;
    let match;
    while ((match = blockRegex.exec(cleaned)) !== null) {
        const selectorGroup = match[1].trim();
        if (!selectorGroup)
            continue;
        const selectors = selectorGroup.split(",");
        for (const rawSel of selectors) {
            const sel = rawSel.trim();
            if (!sel)
                continue;
            // pick last token to approximate target name
            const lastToken = (_a = sel.split(/\s+/).pop()) !== null && _a !== void 0 ? _a : "";
            const target = normalizeCssTarget(lastToken);
            if (!target)
                continue;
            incrementCount(targets, target);
        }
    }
    return targets;
}
function normalizeCssTarget(token) {
    // Strip pseudo-classes/elements: a:hover -> a
    const noPseudo = token.replace(/:{1,2}[a-z0-9_-]+(\([^)]*\))?/gi, "");
    // Strip attribute selectors: a[href] -> a
    const noAttr = noPseudo.replace(/\[[^\]]*\]/g, "");
    // Strip combinators leftovers
    const cleaned = noAttr.trim();
    // Accept common selector forms: element, .class, #id, *
    // For this exercise's examples, "body" and "a" should pass as-is
    if (!cleaned)
        return "";
    return cleaned;
}
function looksLikeCss(text) {
    const cleaned = sanitizeCssNoise(text);
    // heuristic: selector block "something { something: something }"
    return /[^{}]+\{[^{}]+:[^{}]+\}/.test(cleaned);
}
/** shared helper */
function incrementCount(map, key) {
    var _a;
    map[key] = ((_a = map[key]) !== null && _a !== void 0 ? _a : 0) + 1;
}
