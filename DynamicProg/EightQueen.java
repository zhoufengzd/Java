package DynamicProg;

import java.util.ArrayList;

import org.zen.common.def.RowColumn;

/**
 * Eight queens.
 *
 */
public class EightQueen {

	public static void main(String[] args) {
		int size = 9;
		EightQueen eq = new EightQueen(size);
		eq.placeQueens();

		for (ArrayList<RowColumn<Integer>> sol : eq.getSolutions()) {
			String[] board = new String[size];

			for (RowColumn<Integer> p : sol) {
				StringBuilder buffer = new StringBuilder();
				int queenColumn = (int) p.getColumn();
				for (int i = 0; i < size; i++) {
					if (i != queenColumn)
						buffer.append("X|");
					else
						buffer.append("Q|");
				}
				board[(int) p.getRow()] = buffer.toString();
			}

			for (String s : board)
				System.out.println(s);
			System.out.println();
		}
		System.out.printf("Total Count: %d\n", eq.getSolutions().size());
	}

	public EightQueen(int size) {
		_size = size;
		_solutions = new ArrayList<ArrayList<RowColumn<Integer>>>();
	}

	public ArrayList<ArrayList<RowColumn<Integer>>> getSolutions() {
		return _solutions;
	}

	public void placeQueens() {
		doPlaceQueen(0, (new ArrayList<RowColumn<Integer>>()));
	}

	private void doPlaceQueen(int column, ArrayList<RowColumn<Integer>> subSolution) {
		if (column == _size) {
			_solutions.add(subSolution);
			return;
		}

		for (int row = 0; row < _size; row++) {
			if (isValid(row, column, subSolution)) {
				ArrayList<RowColumn<Integer>> newSubSolution = new ArrayList<RowColumn<Integer>>(subSolution);
				newSubSolution.add(new RowColumn<Integer>(row, column));
				doPlaceQueen(column + 1, newSubSolution);
			}
		}
	}

	private boolean isValid(int row, int column, ArrayList<RowColumn<Integer>> queensPlaced) {
		for (RowColumn<Integer> p : queensPlaced) {
			if (row == p.getRow() || (Math.abs(row - p.getRow()) == Math.abs(column - p.getColumn())))
				return false;
		}

		return true;
	}

	private int _size;
	private ArrayList<ArrayList<RowColumn<Integer>>> _solutions;
}
