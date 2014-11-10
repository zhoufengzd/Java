package DynamicProg;

import java.util.ArrayList;

import org.zen.process.SysLogger;

public class MatrixPath {
	private ArrayList<Dot> _path = new ArrayList<Dot>();

	private class Dot {
		private int _x;
		private int _y;

		public Dot(int x, int y) {
			_x = x;
			_y = y;
		}

		public int getX() {
			return _x;
		}

		public void setX(int x) {
			_x = x;
		}

		public int getY() {
			return _y;
		}

		public void setY(int y) {
			_y = y;
		}

		@Override
		public String toString() {
			return String.format("x=%d y=%d", _x, _y);
		}

	}

	public void walkPath(int x, int y) {
		_path.add(new Dot(x, y));

		if (x > 1)
			walkPath(x - 1, y);
		if (y > 1)
			walkPath(x, y - 1);
	}

	public ArrayList<Dot> getPath() {
		return _path;
	}

	public static void main(String[] args) {
		MatrixPath p = new MatrixPath();
		p.walkPath(4, 8);
		SysLogger.println(p.getPath(), "\n");
	}
}
