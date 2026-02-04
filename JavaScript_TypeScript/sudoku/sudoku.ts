type Board = string[][];

function isValid(board: Board, row: number, col: number, d: string): boolean {
  for (let c = 0; c < 9; c++) {
    if (board[row][c] === d) return false;
  }

  for (let r = 0; r < 9; r++) {
    if (board[r][col] === d) return false;
  }

  const boxRowStart = Math.floor(row / 3) * 3;
  const boxColStart = Math.floor(col / 3) * 3;

  for (let r = boxRowStart; r < boxRowStart + 3; r++) {
    for (let c = boxColStart; c < boxColStart + 3; c++) {
      if (board[r][c] === d) return false;
    }
  }

  return true;
}

function solve(board: Board): boolean {
  for (let row = 0; row < 9; row++) {
    for (let col = 0; col < 9; col++) {
      if (board[row][col] !== ".") continue;

      for (let n = 1; n <= 9; n++) {
        const d = String(n);
        if (!isValid(board, row, col, d)) continue;

        board[row][col] = d;
        if (solve(board)) return true;
        board[row][col] = ".";
      }

      return false;
    }
  }

  return true;
}

export function solveSudoku(board: string[][]): void {
  solve(board);
}

function printBoard(board: string[][]): void {
  for (let r = 0; r < 9; r++) {
    const row = board[r].join(" ");
    console.log(row);
  }
  console.log("----------");
}

function solveWithLog(board: string[][], step: { count: number }): boolean {
  for (let row = 0; row < 9; row++) {
    for (let col = 0; col < 9; col++) {
      if (board[row][col] !== ".") continue;

      for (let n = 1; n <= 9; n++) {
        const d = String(n);
        if (!isValid(board, row, col, d)) continue;

        board[row][col] = d;
        step.count += 1;

        if (step.count % 200 === 0) {
          console.log(`step=${step.count}, placed ${d} at (${row},${col})`);
        }

        if (solveWithLog(board, step)) return true;

        board[row][col] = ".";
      }

      return false;
    }
  }

  return true;
}

const demoBoard = [
  ["5","3",".",".","7",".",".",".","."],
  ["6",".",".","1","9","5",".",".","."],
  [".","9","8",".",".",".",".","6","."],
  ["8",".",".",".","6",".",".",".","3"],
  ["4",".",".","8",".","3",".",".","1"],
  ["7",".",".",".","2",".",".",".","6"],
  [".","6",".",".",".",".","2","8","."],
  [".",".",".","4","1","9",".",".","5"],
  [".",".",".",".","8",".",".","7","9"]
];

const step = { count: 0 };
solveWithLog(demoBoard, step);
printBoard(demoBoard);

