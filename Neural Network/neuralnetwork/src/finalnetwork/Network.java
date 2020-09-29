package finalnetwork;

import java.util.Arrays;

public class Network {

    private double[][] output;  //Salida de cada capa [capa][neurona]
    private double[][][] weights;     //Valor de los pesos [capa][neurona][neurona anterior]
    private double[][] bias;    //bias [capa][neurona]

    private double[][] error_signal;
    private double[][] output_derivative;

    public final int[] NETWORK_LAYER_SIZES; //Cuántas neuronas tiene cada capa
    public final int   INPUT_SIZE;  //Cuántos inputs tiene la red
    public final int   OUTPUT_SIZE;     //Cuántos outputs tiene la red
    public final int   NETWORK_SIZE;    //Cuántas capas tiene

    public Network(int... NETWORK_LAYER_SIZES) {
        this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES; //Se pasa por parámetro un array que indica con la longitud la cantidad de capas y con el número la cant de neuronas en esa capa
        this.INPUT_SIZE = NETWORK_LAYER_SIZES[0];   //El tamaño de input es igual a la cantidad de neuronas de la primera capa
        this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length; //El tamaño de la red es la suma de las capas
        this.OUTPUT_SIZE = NETWORK_LAYER_SIZES[NETWORK_SIZE-1]; //El tamaño del output es la cantidad de neuronas en la última capa

        this.output = new double[NETWORK_SIZE][];   //Se crea una array de outputs, pesos y bias con el tamaño de la red (cant de capas) como primera dimensión
        this.weights = new double[NETWORK_SIZE][][];
        this.bias = new double[NETWORK_SIZE][];

        this.error_signal = new double[NETWORK_SIZE][];
        this.output_derivative = new double[NETWORK_SIZE][];

        for(int i = 0; i < NETWORK_SIZE; i++) {
            this.output[i] = new double[NETWORK_LAYER_SIZES[i]];    //La segunda dimensión del output es la cantidad de neuronas en esa capa
            this.error_signal[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.output_derivative[i] = new double[NETWORK_LAYER_SIZES[i]];

            this.bias[i] = NetworkTools.createRandomArray(NETWORK_LAYER_SIZES[i], -0.5,0.7); //Se crea un bias aleatorio
            //CUIDADO: Si el bias o algún peso es cero, va a arruinar las sumas ponderadas y va a ser 0
            if(i > 0) { //Para las capas que no son la primera. La primera no tiene peso
                //Se le asigna al peso de la cada capa por cada neurona teniendo en cuenta la neurona anterior un número aleatorio
                weights[i] = NetworkTools.createRandomArray(NETWORK_LAYER_SIZES[i],NETWORK_LAYER_SIZES[i-1], -1,1);
            }
        }
    }

    public void train(double[] input, double[] target, double learningRate) {    //Recibe los datos de input y los esperados de output
        if(input.length != INPUT_SIZE || target.length != OUTPUT_SIZE) return;
        feedForward(input); //Realiza el feed forward pasándole los valores de input
        backpropError(target);  //Realiza el back propagation con los valores objetivo
        updateWeights(learningRate); //Cambia los pesos con el índice que se le pasa por parámetro
    }


    public double[] feedForward(double... input) {
        if(input.length != this.INPUT_SIZE) return null;    //Control de error. Debe ser igual la cantidad de inputs por neurona que las que recibe el método
        this.output[0] = input; //Para la primera capa no es hace nada
        for(int layer = 1; layer < NETWORK_SIZE; layer ++) {    //Para cada capa exceptuando la primera
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron ++) {   //Para cada neurona

                double sum = bias[layer][neuron];   //Variable que va a almacenar la suma ponderada. Se inicializa con el valor del bias que siempre se muñtiplica por 1
                for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer-1]; prevNeuron ++) { //Por cada neurona de la capa anterior
                    //Se realiza la suma multiplicando el output de la neurona de  la capa anterior * peso[capa actual][neurona actual][neurona anterior]
                    sum += output[layer-1][prevNeuron] * weights[layer][neuron][prevNeuron];
                }
                output[layer][neuron] = sigmoid(sum);   //Una vez se realizaron todas las sumas en esa neurona, se aplica la función de activación (sigmoidea en este caso)
                output_derivative[layer][neuron] = output[layer][neuron] * (1 - output[layer][neuron]); //Se saca la derivada
            }
        }
        return output[NETWORK_SIZE-1];  //Devuelve el output de la útima capa
    }

    private double sigmoid( double x) {
        return 1d / ( 1 + Math.exp(-x));    //Función de activación sigmoidea
    }

    public void backpropError(double[] target) {
        for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[NETWORK_SIZE-1]; neuron ++) {
            //El error de cada neurona será el valor de salida - el valor objetivo multiplicado por la derivada de salida
            error_signal[NETWORK_SIZE-1][neuron] = (output[NETWORK_SIZE-1][neuron] - target[neuron])
                    * output_derivative[NETWORK_SIZE-1][neuron];
        }
        for(int layer = NETWORK_SIZE-2; layer > 0; layer --) {  //For de atrás para adelante sin contar la última capa porque ya se procesó antes
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron ++){    //Por cada neurona
                double sum = 0;
                for(int nextNeuron = 0; nextNeuron < NETWORK_LAYER_SIZES[layer+1]; nextNeuron ++) { //Por cada neurona de la capa siguiente (que ya se calculó)
                    //Primero se suman los pesos de las neuronas de la capa siguiente y se multiplica por el error de la próxima neurona
                    sum += weights[layer + 1][nextNeuron][neuron] * error_signal[layer + 1][nextNeuron];
                }
                //Por último se multiplica la deirvada de la neurona por la suma que conseguimos anteriormente
                this.error_signal[layer][neuron] = sum * output_derivative[layer][neuron];
            }
        }
    }

    public void updateWeights(double learningRate) {
        //Actualizar los pesos
        for(int layer = 1; layer < NETWORK_SIZE; layer++) { //Por cada neurona sin contar la de input
            for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {    //Por cada neurona

                //se multiplica -velocidad de aprendizaje (cuánto se "mueve" cada vez que evalúa) por el error de cada neurona para que sea más fino al estar más cerca
                //Se actualiza el bias
                double delta = - learningRate * error_signal[layer][neuron];
                bias[layer][neuron] += delta;

                for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer-1]; prevNeuron ++) { //Por cada neurona de la capa anterior (que está conectada a la neurona actual)
                    //Se actualizan los pesos como el bias pero teniendo en cuenta el output de la neurona anterior
                    weights[layer][neuron][prevNeuron] += delta * output[layer-1][prevNeuron];
                }
            }
        }
    }

    public void saveNetwork(String fileName) throws Exception {
        //Método para guardar la red neuronal una vez fue entrenada
        //Guarda los pesos y los bias por cada capa en el archivo pasado como parámetro
        Parser p = new Parser();
        p.create(fileName);
        Node root = p.getContent();
        Node netw = new Node("Network");
        Node ly = new Node("Layers");
        netw.addAttribute(new Attribute("sizes", Arrays.toString(this.NETWORK_LAYER_SIZES)));
        netw.addChild(ly);
        root.addChild(netw);
        for (int layer = 1; layer < this.NETWORK_SIZE; layer++) {

            Node c = new Node("" + layer);
            ly.addChild(c);
            Node w = new Node("weights");
            Node b = new Node("biases");
            c.addChild(w);
            c.addChild(b);

            b.addAttribute("values", Arrays.toString(this.bias[layer]));

            for (int we = 0; we < this.weights[layer].length; we++) {

                w.addAttribute("" + we, Arrays.toString(weights[layer][we]));
            }
        }
        p.close();
    }

    public static Network loadNetwork(String fileName) throws Exception {
        //Carga el archivo pasado por parámetro y devuelve una red neuronal a la que se le asingan
        //Los pesos y biases que están descritos en el archivo
        Parser p = new Parser();

            p.load(fileName);
            String sizes = p.getValue(new String[] { "Network" }, "sizes");
            int[] si = ParserTools.parseIntArray(sizes);
            Network ne = new Network(si);

            for (int i = 1; i < ne.NETWORK_SIZE; i++) {
                String biases = p.getValue(new String[] { "Network", "Layers", new String(i + ""), "biases" }, "values");
                double[] bias = ParserTools.parseDoubleArray(biases);
                ne.bias[i] = bias;

                for(int n = 0; n < ne.NETWORK_LAYER_SIZES[i]; n++){

                    String current = p.getValue(new String[] { "Network", "Layers", new String(i + ""), "weights" }, ""+n);
                    double[] val = ParserTools.parseDoubleArray(current);

                    ne.weights[i][n] = val;
                }
            }
            p.close();
            return ne;

    }

}
