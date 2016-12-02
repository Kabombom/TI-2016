import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;
import java.util.Enumeration;

public class MyGIFEncoder {
	private short width, height; // largura e altura da imagem
	private int numColors; // numero de cores distintas na imagem
	private byte pixels[]; // array com os indices de cores, i.e., array com a imagem indexada
	private byte colors[]; // array 3 vezes maior que o anterior com os niveis RGB da imagem associados a cada indice (cores a escrever na Global Color Table)
	private byte [][] r, g, b; // matrizes com os valores R,G e B em cada celula da imagem
	private byte minCodeSize; // tamanho minimo dos codigos LZW
	private byte cc, eoi; // Codigos
	private int usedBits = 0; //Bits usados
	private int availableBits = 8; 	// Bits disponiveis
	private int availableSubBlock = 255; // Bits disponiveis no sub block com o block size adicionado
	private int codeSize;
	private byte toBeInserted = (byte)0x00;
	private byte subBlockSize = (byte)0xFF;
    private Hashtable<Integer, String> codificationTable; // HashTable for LZW algorithm
	private int availableAlphabetEntry;

	// Construtor e funcoes auxiliares (para obtencao da imagem indexada)
	public MyGIFEncoder(Image image) throws InterruptedException, AWTException {
		width = (short)image.getWidth(null);
		height = (short)image.getHeight(null);
        codificationTable = new Hashtable<Integer, String>();

		// Definir a imagem indexada
		getIndexedImage(image);
	}

	// Conversao de um objecto do tipo Image numa imagem indexada
	public void getIndexedImage(Image image) throws InterruptedException, AWTException {
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
	public void getRGB(int [] values) throws AWTException {
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
	public void RGB2Indexed() throws AWTException {
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
	public int nextPower2(int n) {
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
	public byte numBits(int n) {
		byte nb = 0;
		if (n == 0)
			return 0;
		while (n != 0) {
			nb++;
			n /= 2;
		}
		return nb;
	}

	public int resetAlphabet() {
		int i = 0;

		codificationTable = new Hashtable<Integer, String>();

    	while(i < colors.length) {
			codificationTable.put(i/3, Integer.toString(i/3) );
			i += 3;
        }

        // Clear Code -> 2^N
		cc = (byte)Math.pow(2, minCodeSize);
        codificationTable.put(i/3, Integer.toString(cc));

        // End Of Information -> 2^N + 1
		eoi = (byte)(cc + 1);
        codificationTable.put(i/3 + 1, Integer.toString(eoi));
        return eoi + 1;
    }

	public int keyOfValue(Hashtable hash, Object value) {
		Enumeration e = hash.keys();
		int key = 0;
		if(!hash.contains(value.toString())) {
			return -1;
		}
		while (e.hasMoreElements()) {
			key = (Integer) e.nextElement();
			if(hash.get(key).equals(value.toString()) ) {
				break;
			}
		}
		return key;
	}

	public void pauseProg(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}


	/* Status
	 0 -> (byte nao preenchido) verificar se há mais numeros para serem inseridos
			 se sim, continuar
			se nao, inserir eoi e preencher sub bloco com 0's
	 1 -> (byte preenchido) verificar se vao ser inseridos mais numeros
			se sim, verificar se e o fim do sub bloco
				se sim, inserir sub block size
				se nao, continuar
			se nao, verificar se e o fim do sub bloco
				se sim, inserir sub block size, inserir eoi, e preencher com 0's
				se nao, inseir eoi e preencher com 0's
	*/

	public int writeOnOutput(OutputStream output, int num) throws IOException {

		byte temp = (byte)0x00;
		int inNum = num;
		int reqBits;

		//System.out.println("num: " + num + " " + Integer.toBinaryString(num));
		reqBits = codeSize;
		// System.out.println("codeSize: " + codeSize);

		/* O numero de bits necessario para representar inNum é menor ou igual a codeSize
		Enquanto tiver bits para representar */

		while(inNum > 0) {
			temp = (byte)((inNum << usedBits) & 0xFF);
			toBeInserted = (byte)((temp | toBeInserted) & 0xFF);

			// Se o numero de bits a adicionar foi maior ou igual que o numero de bits disponiveis no byte
			if(reqBits >= availableBits) {
				// System.out.println("Primeiros " + availableBits + " bits de " + Integer.toBinaryString(inNum) + " foram inseridos no byte anterior");
				inNum = (inNum >> availableBits);    // Update inNum, contendo os bits nao adicionados
				output.write(toBeInserted);

				System.out.println("Block index:" + (256 - availableSubBlock) +  " - Byte: " + toBeInserted + " - bitsPerCode: " + codeSize + " num " + num);

				// System.out.println("Enviar " + fixByte(toBeInserted) + " " + Integer.toBinaryString(fixByte(toBeInserted)));
				toBeInserted = (byte)0x00;
				reqBits -= availableBits;
				availableBits = 8;
				availableSubBlock -= 1;
				usedBits = 0;

				if (availableSubBlock == 0) {
					if (reqBits > 0) {
						// Se ainda tenho bits para adicionar
						// System.out.println("Subblock acabou");
						output.write(subBlockSize);
						availableSubBlock = 255;
					}
					else {
						// Se todos os bits foram adicionados devolver 1 e verificar se vao ser adicionados mais bytes
						//System.out.println("Subblock acabou");
						return 1;
					}
				}
			}
			else { // Se o numero de bits necessario para representar o num for menor que o numero de availableBits, inserir numero
				//System.out.println("Primeiros " + reqBits + " bits de " + Integer.toBinaryString(inNum) + " foram inseridos no byte");
				inNum = (inNum >> availableBits);    // Update inNum, contendo os bits nao adicionados
				temp = (byte)((inNum << usedBits) & 0xFF);
				toBeInserted = (byte)((temp | toBeInserted) & 0xFF);
				inNum = inNum >> codeSize;
				usedBits += reqBits;
				availableBits -= reqBits;
				reqBits = 0;
			}
		}

		// Se o numero de bits necessarios para representar o num for menor que o code size devemos preencher os restantes bits com 0's
		while(reqBits > 0) {
            if(reqBits >= availableBits) {
				//System.out.println(Integer.toBinaryString(fixByte(toBeInserted)) + " " + toBeInserted + " Byte inserido. foram inseridos " + availableBits + " 0's");
				output.write(toBeInserted);

				System.out.println("Block index:" + (256 - availableSubBlock) + " - Byte: " + toBeInserted + " - bitsPerCode: " + codeSize + " num " + num);

				//System.out.println("Enviar " + fixByte(toBeInserted) + " " + Integer.toBinaryString(fixByte(toBeInserted)));
				toBeInserted = (byte)0x00;

				reqBits -= availableBits;
				availableBits = 8;
				availableSubBlock -= 1;
				//System.out.println("availableBits: " + availableBits + " reqBits: " + reqBits);

                if(availableSubBlock == 0 && reqBits > 0) { // Se ainda ha bits para adicionar e o subbloc acabou insere-se o block size
					//System.out.println("Sub block acabou");
					output.write(subBlockSize);
					availableSubBlock = 255;
				}

				if(availableSubBlock == 0 && reqBits == 0) { // O byte foi preenchido e inserido, sub block esgotado
					//System.out.println("Sub block acabou");
					return 1;
				}

				if(reqBits==0 && usedBits==0) { //O byte foi totalmente preenchido e o numero totalmente inserido
					return 1;
				}

			}
			else { // Ultimo byte nao foi preenchido nem inserido
				//System.out.println("Foram inseridos " + reqBits + " 0's");
				availableBits -= reqBits;
				usedBits = 8 - availableBits;
				reqBits = 0;
			}
		}

		//System.out.println("usedBits: " + usedBits + " availableBits: " + availableBits + " toBeInserted: " + Integer.toBinaryString(fixByte(toBeInserted)));
		return 0; // Ultimo byte nao preenchido e nao inserido
	}

	// O byte apenas suporta numeros de 0 a 128 e de -127 a 0
	public int fixByte(byte n) {
		int fixedByte;
		if((int)n < 0) {
            fixedByte = 256 + (int)n;
        } else fixedByte = (int)n;
		return fixedByte;
	}

	public void lzwCodification(OutputStream output) throws IOException {
		// Escrever blocos com 256 bytes no maximo
		// Escrever blocos comprimidos, com base na matriz pixels e no minCodeSize;
		// O primeiro bloco tem, depois do block size, o clear code
		// Escrever end of information depois de todos os blocos

		// Para o LZW
		// Colors e o alfabeto
		// Pixels a mensagem

        int cat; // Variavel usada para indice de concatenaçoes
        int i = 0;
        int currentPixel;
        int nextPixel;
		String prevColor;
        String color;
        String nextColor;
        int prevIndex;
		int freeze = 0;

        codeSize = minCodeSize + 1;
		int maxValue = (int) Math.pow(2, codeSize);

        // Cria dicionario inicial com CC e EOI, e devolve proximo index livre
        int availableAlphabetEntry = resetAlphabet();

        // Inserir block size - 255 (admitimos que nao vamos adicionar uma imagem vazia)
        output.write(subBlockSize);

        // Inserir clear code (pode-se ignorar o return da funcao porque se referiu em cima que o sub block size ia ser 255)
    	writeOnOutput(output, cc);

	    while(i < pixels.length) {
	        currentPixel = pixels[i];

	        color = codificationTable.get(currentPixel);

	        cat = 0;
			prevIndex =	currentPixel;

	        while(i + cat + 1 != pixels.length) {
	            cat += 1;
	            nextPixel = pixels[i + cat];
	            nextColor = codificationTable.get(nextPixel);
	            color = color.concat("|" + nextColor);

	            if(!codificationTable.contains(color)) {
								if(availableAlphabetEntry >= 4096) {
									freeze=1;
									//codeSize = minCodeSize + 1;
									//writeOnOutput(output, cc);
									//availableAlphabetEntry = resetAlphabet();
								}
								if(freeze==0) {
									if (availableAlphabetEntry - 1 == maxValue) {
										codeSize++;
										maxValue = (int) Math.pow(2, codeSize);
									}

									codificationTable.put(availableAlphabetEntry, color);
									availableAlphabetEntry += 1;
								}
	              break;
	            }
	            else {
					prevColor = color;
					prevIndex = keyOfValue(codificationTable, prevColor);
	            }
	        }

			switch(writeOnOutput(output, prevIndex)) {
				case 0: // Numero inserido
					if(i + 1 == pixels.length) { // Se nao houver mais numeros
						switch (writeOnOutput(output, eoi)) { // Inserir EOI e atualizar byte
							case 0: // Ultimo byte nao preenchido
								codeSize = availableBits;
								writeOnOutput(output, 0); //Podemos ignorar returns pois como codeSize é igual ao availableBits, o byte será preenchido com 0's
								writeZeros(output);
								return;
							case 1: // ultimo byte foi preenchiddo
								if (availableSubBlock != 0) { // Verificar se é o final do sub bloco
									writeZeros(output);
								}
								return; //Sair da funçao e inserir block terminator e trailer
						}
					} // Se houver mais pixeis continuar

				case 1: // byte preenchido
					if(i + 1 == pixels.length) { // Se nao vao ser inseridos mais numeros
						if(availableSubBlock == 0) {	// Se e no fim do bloco
							output.write(subBlockSize);
							availableSubBlock = 255;
						}

						System.out.println("EIO");
						switch(writeOnOutput(output, eoi)) { // Escrever eoi e atualizar byte
							case 0: // byte nao preenchido
								codeSize = availableBits;
								writeOnOutput(output, 0); 	// Pode-se ignorar returns pois como codeSize é igual ao availableBits, o byte será preenchido com 0's
								writeZeros(output);		// Acaba o bloco com 0s
								return;

							case 1: //byte preenchido
								writeZeros(output);
								return;
						}
					}
					else {
						if(availableSubBlock == 0) {	// Se e no fim do sub bloco
							output.write(subBlockSize);
							availableSubBlock = 255;
						}
					}
			}
			//pauseProg(1);
			i = i + cat;
		}
	}

	public void writeZeros(OutputStream output) throws IOException {
		//Preencher sub bloco com 0's
		while(availableSubBlock > 0)  {
			output.write((byte)0x00);
			availableSubBlock -= 1;
		}
	}

	// Funcao para escrever imagem no formato GIF, versao 87a
    public void write(OutputStream output) throws IOException {
		// Escrever cabecalho do GIF
		writeGIFHeader(output);

		// Escrever cabecalho do Image Block -> Img Block Header + min code size
		writeImageBlockHeader(output);

        lzwCodification(output);

        // Escrever block terminator (0x00)
        byte terminator = (byte)0x00;
        output.write(terminator);

		// Trailer
		byte trailer = (byte)0x3b;
		output.write(trailer);

		// Flush do ficheiro (BufferedOutputStream utilizado)
		output.flush();
	}


	// Gravar cabecalho do GIF (ate global color table)
	public void writeGIFHeader(OutputStream output) throws IOException {
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
	public void writeImageBlockHeader(OutputStream output) throws IOException {
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
}
