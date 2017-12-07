package cmm;

public class test {
	public static void main(String[] args) {
		int headHigh = 6;
		int acuteHigh = 25;
		int border = 50;
		for (int i = 1; i <= headHigh + acuteHigh; i++) {
			for (int j = 1; j <= border; j++) {
				// 上三角
				if (i <= headHigh) {
					if (j >= (border / 2 + 1) + 1 - i && j <= (border / 2 + 1) - 1 + i) {
						System.out.print("*");
					} else {
						System.out.print("-");
					}
				}
				// 上三角一下部分
				if (i > headHigh && i <= acuteHigh) {
					if (j >= (border / 2 + 1) + 1 - i && j <= border - 3 * (i - headHigh)) {
						System.out.print("*");
					} else if (j <= (border / 2 + 1) - 1 + i && j >= 0 + 3 * (i - headHigh)) {
						System.out.print("*");
					} else {
						System.out.print("-");
					}
				}
			}
			System.out.println("");
		}
	}
}
