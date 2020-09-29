package finalnetwork;

public class ParserTools {

	public static int[] parseIntArray(String code) {
		code = code.substring(1, code.length()-1);
		String[] data = code.split(",");
		int[] d = new int[data.length];
		for(int i = 0; i <d.length; i++){
			d[i] = Integer.parseInt(data[i].trim());
		}
		return d;
	}

	public static double[] parseDoubleArray(String code) {
		code = code.substring(1, code.length()-1);
		String[] data = code.split(",");
		double[] d = new double[data.length];
		for(int i = 0; i <d.length; i++){
			d[i] = Double.parseDouble(data[i].trim());
		}
		return d;
	}

	public static String createSpaces(int amount) {
		String res = "";
		for (int i = 0; i < amount; i++) {
			res += " ";
		}
		return res;
	}
}
