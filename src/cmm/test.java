package cmm;

public class test {
	public static void main(String[] args) {
		int headHigh = 6;
		int acuteHigh = 25;
		int border = 50;
		int i = 1;
		while(i<= headHigh + acuteHigh){
			int j = 1;
			while(j <= border){
				// 上三角
				if (i <= headHigh) {
					if (j >= (border / 2 + 1) + 1 - i && j <= (border / 2 + 1) - 1 + i) {
						System.out.print("*");
					} else {
						System.out.print(" ");
					}
				}
				// 上三角一下部分
				if (i > headHigh && i <= acuteHigh) {
					if (j >= (border / 2 + 1) + 1 - i && j <= border - 3 * (i - headHigh)) {
						System.out.print("*");
					} else if (j <= (border / 2 + 1) - 1 + i && j >= 0 + 3 * (i - headHigh)) {
						System.out.print("*");
					} else {
						System.out.print(" ");
					}
				}
				j =j+1;
			}
			System.out.println("");
			i = i+1;
		}
	}
}

