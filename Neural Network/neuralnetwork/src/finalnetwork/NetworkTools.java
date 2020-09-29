package finalnetwork;

public class NetworkTools {


    public static double[] createRandomArray(int size, double lower_bound, double upper_bound) {
        //Método que crea un array lleno de numeros aleatorios entre límites
        if (size < 1) {
            return null;
        }
        double[] ar = new double[size]; //El array tiene la longitud que se le pasa por parámetro al método
        for (int i = 0; i < size; i++) {
            ar[i] = randomValue(lower_bound, upper_bound);   //Se llena con datos aleatorios
        }
        return ar;
    }

    public static double[][] createRandomArray(int sizeX, int sizeY, double lower_bound, double upper_bound) {
        //Crea un array de 2D aleatorio
        if (sizeX < 1 || sizeY < 1) {
            return null;
        }
        double[][] ar = new double[sizeX][sizeY];
        for (int i = 0; i < sizeX; i++) {
            ar[i] = createRandomArray(sizeY, lower_bound, upper_bound);
        }
        return ar;
    }

    public static double randomValue(double lower_bound, double upper_bound) {
        //Método que devuelve un número aleatorio entre dos límites
        return Math.random() * (upper_bound - lower_bound) + lower_bound;
    }
}
