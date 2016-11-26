import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.lang.Math.*;

// Para o LZW
// Colors e o alfabeto
// Pixels a mensagem

public class MyGIFEncoder {
	private short width, height; // largura e altura da imagem
	private int numColors; // numero de cores distintas na imagem
	private byte pixels[]; // array com os indices de cores, i.e., array com a imagem indexada
	private byte colors[]; // array 3 vezes maior que o anterior com os niveis RGB da imagem
    // associados a cada indice (cores a escrever na Global Color Table)
	private byte [][] r, g, b; // matrizes com os valores R,G e B em cada celula da imagem
	private byte minCodeSize; // tamanho minimo dos codigos LZW
	//Codigos
	private int cc, eoi;
	//Bits usados
	private int usedBits = 0;
	//bits disponiveis
	private int availableBits = 8;
	//bits disponiveis no sub block
	private int availableSubBlock = 256;
	//byte que vai ser inserido
	private int toBeInserted;
	//numero temporario
	private int tempNum;
	//Code Size
	private int codeSize;
	//valor correspondente ao valor maximo que o code size pode ter -> 3 * nextPower2(minCodeSize)
	private int maxValue;
	// associados a cada indice (cores a escrever na Global Color Table)
    private Hashtable<Integer, String> codificationTable; //HashTable for LZW algorithm

	// Construtor e funcoes auxiliares (para obtencao da imagem indexada)
	public MyGIFEncoder(Image image) throws InterruptedException, AWTException {
		width = (short)image.getWidth(null);
		height = (short)image.getHeight(null);
        codificationTable = new Hashtable<Integer, String>();

		// Definir a imagem indexada
		getIndexedImage(image);
	}

	// Conversao de um objecto do tipo Image numa imagem indexada
	private void getIndexedImage(Image image) throws InterruptedException, AWTException {
		// Matriz values: cada entrada contem um inteiro com 4 conjuntos de 8 bits,
		// pela seguinte ordem: alpha, red, green, blue
		// obtidos com o metodo grabPixels da classe PixelGrabber
		int values[] = new int[width * height];
		PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, values, 0, width);
    	grabber.grabPixels();

		// Obter imagem RGB
		getRGB(values);

		// Converter para imagem indexada
		RGB2Indexed();
	}

	// Obtencao dos valores RGB a partir dos valores lidos pelo PixelGrabber no metodo anterior
	private void getRGB(int [] values) throws AWTException {
		r = new byte[width][height];
		g = new byte[width][height];
		b = new byte[width][height];

		int index = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				r[x][y] = (byte) ((values[index] >> 16) & 0xFF);
				g[x][y] = (byte) ((values[index] >> 8) & 0xFF);
				b[x][y] = (byte) ((values[index]) & 0xFF);
				index++;
			}
		}
	}

	// Conversao de matriz RGB para indexada: maximo de 256 cores
	private void RGB2Indexed() throws AWTException {
		pixels = new byte[width * height];
		colors = new byte[256 * 3];
		int colorNum = 0;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int index;
				for (index = 0; index < colorNum; index++) {
					if (colors[index * 3] == r[x][y] &&
							colors[index * 3 + 1] == g[x][y] &&
							colors[index * 3 + 2] == b[x][y])
						break;
				}

				if (index > 255) {
					System.out.println("Demasiadas cores...");
					System.exit(-1);
				}

				pixels[y * width + x] = (byte)index;

				if (index == colorNum) {
					colors[index * 3] = r[x][y];
					colors[index * 3 + 1] = g[x][y];
					colors[index * 3 + 2] = b[x][y];
					colorNum++;
				}
			}
		}

		// Define o numero de cores como potencia de 2 (devido aos requisitos da Global Color Table)
		numColors = nextPower2(colorNum);

		// Refine o array de cores com base no numero final obtido
		byte copy[] = new byte[numColors * 3];
		System.arraycopy(colors, 0, copy, 0, numColors * 3);
		colors = copy;
	}

	// Determinacao da proxima potencia de 2 de um dado inteiro n
	private int nextPower2(int n) {
		int ret = 1, nIni = n;
		if (n == 0)
			return 0;
		while (n != 0) {
			ret *= 2;
			n /= 2;
		}
		if (ret % nIni == 0)
			ret = nIni;
		return ret;
	}

	// Numero de bits necessario para representar n
	private byte numBits(int n) {
		byte nb = 0;
		if (n == 0)
			return 0;
		while (n != 0) {
			nb++;
			n /= 2;
		}
		return nb;
	}

	private int resetAlphabet() {
		String fullColor = "";
		int i = 0;

        while(i < colors.length) {
			fullColor += Byte.toString(colors[i]) + ".";
			fullColor += Byte.toString(colors[i+1]) + ".";
			fullColor += Byte.toString(colors[i+2]);
            codificationTable.put(i/3, fullColor);
			fullColor = "";
			i+=3;
        }

        // Clear Code -> 2^N
		cc = (int)Math.pow(2,minCodeSize);
        codificationTable.put(i/3, Integer.toString(cc));
        // End Of Information -> 2^N + 1
		eoi = cc + 1;
        codificationTable.put(i/3 + 1, Integer.toString(eoi));
        return eoi + 1;
    }

	private int keyOfValue(Hashtable hash, Object value) {
		Enumeration e = hash.keys();
 		int key = 0;

		if(!hash.contains(value.toString())) {
		   return -1;
		}

		while (e.hasMoreElements()) {
 		   key = (int)e.nextElement();
 		   if(hash.get(key).equals(value.toString()) ) {
 				break;
 			}
		}

 		return key;

		/*
		Enumeration keys = hash.keys();
 		//System.out.println("VALUE " + value.toString());
 		int i = 0;
 		while(keys.hasMoreElements()) {
 			String valueFromHash = (String) hash.get(keys.nextElement());
 			//System.out.println("Value from Hash " + valueFromHash);
 			if (valueFromHash.equals(value.toString())) {
 				return i;
 			}
 			i++;
 		}
		return 0;
		*/
	}

	private void pauseProg(int sec) {
		try {
				Thread.sleep(sec * 1000);
		} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
		}
	}

	private String numToBitString(int num) {
		String out = "";
		int nbits = 0;

		while(nbits<8) {
			if(num%2 != 0) {
				out = new String("1" + out);
			} else {
				out = new String("0" + out);
			}
			num = num / 2;
			++nbits;
		}

		return out;
	}

	//Escreve numero no output
	private int writeOnOutput(OutputStream output, String output_str, int num) throws IOException {
		/*
		Status
		-1 -> reenviar numero antes de concatenaçoes
		 0 -> (byte nao preenchido) verificar se vao ser inseridos mais numeros,
		 	  	se nao, preencher resto do sub block com 0's e inserir eoi
			  	se sim, inserir sub block size e availableSubBlock = (sub block size) ao output e continuar
		 1 -> (byte preenchido) verificar se vao ser inseridos mais numeros,
		 		se nao, inserir eoi
		 		se sim, inserir sub block size e availableSubBlock = (sub block size) ao output e continuar
		*/

		private int temp;
		private int inNum;

		inNum = num;

		//Update codeSize se o num for maior que 2^(codeSize) - 1
		if( numBits(num) > codeSize ) {
			codeSize +=1;
		}

		//Reset do dicionario se codeSize for maior que 3 * 2^(minCodeSize + 1) e adicionar CC ao output
		if( inNum >= maxValue ) {
			codificationTable = resetAlphabet();
			codeSize = minCodeSize;
			//Inserir clear code no output
			reqBits = numBits(cc);
			writeOnOutput(output, output_str, cc);

			return -1 //Sinal para reenviar num apos reset no dicionario
		}

		reqBits = codeSize;

		//O numero de bits necessario para representar inNum é menor ou igual a codeSize
		//Enquanto tiver bits para representar
		while(inNum > 0) {
			//Bits a adicionar
			temp = inNum << usedBits;
			//Preencher byte
			toBeInserted = (byte)((temp | toBeInserted) & 0xFF);
			//Update inNum, contendo os bits nao adicionados
			inNum = inNum >> availableBits;
			//Se o numero de bits a adicionar foi maior ou igual que o numero de bits disponiveis no byte
			if(reqBits >= availableBits) {
				output.write(toBeInserted);
				output_str = output_str.concat("\n" + numToBitString(toBeInserted));
				reqBits -= availableBits;
				availableBits = 8;
				usedBits = 0;
				availableSubBlock -= 8;
				if(availableSubBlock == 0 && reqBits > 0) {
					//Se ainda tenho bits para adicionar
					output.write(0xFF);
					output_str = output_str.concat("\n" + numToBitString(0xFF));
					availableSubBlock = 256;
				} else if(availableSubBlock == 0 && reqBits == 0) {
					//Se todos os bits foram adicionados devolver 1 e verificar se vao ser adicionados mais bytes
					availableSubBlock = 256;
					return 1;
				}
			} else reqBits = 0;
		}
		//Se ainda o numero fe bits necessario para representar o num for menor que o code size devemos preencher os restantes bits com 0's
		while(reqBits > 0) {
			if(reqBits >= availableBits) {
				output.write(toBeInserted);
				output_str = output_str.concat("\n" + numToBitString(toBeInserted));
				reqBits -= availableBits;
				availableBits = 8;
				usedBits = 0;
				availableSubBlock -= 8;
				if(availableSubBlock == 0 && reqBits > 0) {
					//Se ainda tenho bits para adicionar
					output.write(0xFF);
					output_str = output_str.concat("\n" + numToBitString(0xFF));
					availableSubBlock = 256;
				} else if(availableSubBlock == 0 && reqBits == 0) {
					//Se todos os bits foram adicionados devolver 1 e verificar se vao ser adicionados mais bytes
					availableSubBlock = 256;
					return 1;
				}
			} else {
				usedBits += reqBits;
				availableBits -= reqBits;
				reqBits = 0;
				//Se o ultimo byte nao esta preenchido devolver 0 e verificar se vao haver mais numeros
				return 0;
			}
		}
	}

	private void lzwCodification(OutputStream output) throws IOException {
		// Escrever blocos com 256 bytes no maximo
		// Escrever blocos comprimidos, com base na matriz pixels e no minCodeSize;
		// O primeiro bloco tem, depois do block size, o clear code
		// Escrever end of information depois de todos os blocos
		int currentPixel;
		int cat;
		int nextPixel;
		String output_str = "";
		int i = 0;
		String color;
		String nextColor;
		String prevColor;

		//valor correspondente ao valor maximo que o code size pode ter -> 3 * 2^(minCodeSize + 1)
		maxValue = 3 * nextPower2(minCodeSize);
		//primeiro codesize sera minCodeSize + 1
		codeSize = minCodeSize+1;
		// Cria dicionario inicial com CC e EOI, e devolve proximo index livre
		int availableAlphabetEntry = resetAlphabet();
		//Inserir block size - 256 -> admitimos que nao vamos adicionar uma imagem vazia
		output.write((byte)0xFF);
		//Inserir clear code -> poderemos ignorar o return da Funcao pq inserimos a cima que o sub block size ia ser 256
		writeOnOutput(output, output_str, cc);
		// Inserir CC depois de cada sub-block e EOI no ultimo bloco

        while(i < 10) {
            currentPixel = pixels[i];
            // percet = ( ( ((float)i) + 1 ) / pixels.length) * 100;
            // System.out.println(percet + "% Completed i= " + i + " max= " +  pixels.length);
            //System.out.println("\ni = " + i + " Searching for key " + currentPixel + " in dictionary");
            color = codificationTable.get(currentPixel);
            //System.out.println("Color: " + color);
            //Check cads
            cat = 0;
            while(i + cat != pixels.length) {
                cat += 1;
                nextPixel = pixels[i + cat];
                prevColor =	color;
                nextColor = codificationTable.get(nextPixel);
                color = color.concat("-" + nextColor);
                //System.out.println("Color from concat: " + color );
                //System.out.println("Searching for color " + color + " in dictionary");
                if(!codificationTable.contains(color)) {
                    System.out.println("Color not found adding to dictionary at \nSending color " + prevColor + " at " + keyOfValue(codificationTable, prevColor));
					tempNum = keyOfValue(codificationTable, prevColor);
                    codificationTable.put(availableAlphabetEntry, color);
                    availableAlphabetEntry += 1;
					//TO DO -> aplicar mudanças dependendo do valor de return
					writeOnOutput(output, output_str, tempNum);
                    break;
                } /*else {
                    System.out.println("Color found");
                }
                pauseProg(1);*/
            }
            ++i;
        }
		System.out.println(output_str);
		//System.out.print(codificationTable);
	}

	// Funcao para escrever imagem no formato GIF, versao 87a
    public void write(OutputStream output) throws IOException {
		// Escrever cabecalho do GIF

		writeGIFHeader(output);

		// Escrever cabecalho do Image Block -> Img Block Header + min code size
		writeImageBlockHeader(output);

        lzwCodification(output);

        // Escrever block terminator (0x00)
        char terminator = 0x00;
        output.write(terminator);

		// Trailer
		byte trailer = 0x3b;
		output.write(trailer);

		// Flush do ficheiro (BufferedOutputStream utilizado)
		output.flush();
	}


	// Gravar cabecalho do GIF (ate global color table)
	private void writeGIFHeader(OutputStream output) throws IOException {
		// Assinatura e versao (GIF87a)
		String s = "GIF87a";

		for (int i = 0; i < s.length(); i++) {
			output.write((byte) (s.charAt(i)));
		}

		// Ecra logico (igual a da dimensao da imagem) -> primeiro o LSB e depois o MSB
		output.write((byte)(width & 0xFF));
		output.write((byte)((width >> 8) & 0xFF));
		output.write((byte)(height & 0xFF));
		output.write((byte)((height >> 8) & 0xFF));

		// GCTF, Color Res, SF, size of GCT
		byte toWrite, GCTF, colorRes, SF, sz;
		GCTF = 1;
		colorRes = 7;  // Numero de bits por cor primaria (-1)
		SF = 0;
		sz = (byte) (numBits(numColors - 1) - 1); //-1: 0 -> 2^1, 7 -> 2^8
		toWrite = (byte) (GCTF << 7 | colorRes << 4 | SF << 3 | sz);
		output.write(toWrite);

		// Background color index
		byte bgci = 0;
		output.write(bgci);

		// Pixel aspect ratio
		byte par = 0; // 0 -> informacao sobre aspect ratio nao fornecida -> decoder usa valores por omissao
		output.write(par);

		// Global color table
		output.write(colors, 0, colors.length);
	}


	// Gravar cabecalho do Image Block (LZW minimum code size)
	private void writeImageBlockHeader(OutputStream output) throws IOException {
		// Image separator
		byte imSep = 0x2c;
		output.write(imSep);

		// Image left, top, width e height
		byte left = 0, top = 0;
		output.write((byte)(left & 0xFF));
		output.write((byte)((left >> 8) & 0xFF));
		output.write((byte)(top & 0xFF));
		output.write((byte)((top >> 8) & 0xFF));
		output.write((byte)(width & 0xFF));
		output.write((byte)((width >> 8) & 0xFF));
		output.write((byte)(height & 0xFF));
		output.write((byte)((height >> 8) & 0xFF));

		// LCTF, Interlace, SF, reserved, size of LCT
		byte toWrite, LCTF, IF, res, SF, sz;
		LCTF = 0;IF = 0;
		SF = 0;
		res = 0;
		sz = 0;
		toWrite = (byte) (LCTF << 7 | IF << 6 | SF << 5 | res << 4 | sz);
		output.write(toWrite);

		// Local Color Table: nao definida

		// LZW Minimum Code Size (com base no numero de cores utilizadas)
		minCodeSize = (numBits(numColors - 1));
		if (minCodeSize == 1) {  // Imagens binarias -> caso especial (pag. 26 do RFC)
			minCodeSize++;
		}

		output.write(minCodeSize);
	}

	public void printColorArray() {
		for(int e = 0; e + 2 < colors.length; e += 3) {
            System.out.print(colors[e] + " " + colors[e+1] + " " + colors[e+2] + "\n");
		}
	}
}
