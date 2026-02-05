declare module 'stats' {
    export function getMaxIndex<T>(
        input: T[],
        comparator: (a: T, b: T) => number
    ): number;
}
