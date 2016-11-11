import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Hashtable;

// Para o LZW
// Colors e o alfabeto
// Pixels a mensagem

public class MyGIFEncoder {
	short width, height; // largura e altura da imagem
	int numColors; // numero de cores distintas na imagem
	byte pixels[]; // array com os indices de cores, i.e., array com a imagem indexada
	byte colors[]; // array 3 vezes maior que o anterior com os niveis RGB da imagem
    // associados a cada indice (cores a escrever na Global Color Table)
	byte [][] r, g, b; // matrizes com os valores R,G e B em cada celula da imagem
	byte minCodeSize; // tamanho minimo dos codigos LZW
	// associados a cada indice (cores a escrever na Global Color Table)
    Hashtable<Integer, Integer> codificationTable; //HashTable for LZW algorithm


	// Construtor e funcoes auxiliares (para obtencao da imagem indexada)
	public MyGIFEncoder(Image image) throws InterruptedException, AWTException {
		width = (short)image.getWidth(null);
		height = (short)image.getHeight(null);
        codificationTable = new Hashtable<Integer, Integer>();

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


	private void generateInitialEntriesForLzw() {
        for (int i = 0; i < colors.length; i++) {
            codificationTable.put(i+1, (int) colors[i]);
        }
    }

	public void lzwCodification() {
		// Escrever blocos com 256 bytes no maximo
		// CODIFICADOR LZW AQUI !!!!
		// Escrever blocos comprimidos, com base na matriz pixels e no minCodeSize;
		// O primeiro bloco tem, depois do block size, o clear code
		// Escrever end of information depois de todos os blocos
        generateInitialEntriesForLzw();
        int currentInputCharacter = (int)pixels[0];

        for (int i = 1; i < pixels.length; i++) {
            currentInputCharacter = currentInputCharacter << 8;
            int sequence = currentInputCharacter + (int)pixels[i];

        }
	}

	// Funcao para escrever imagem no formato GIF, versao 87a
	// COMPLETAR ESTA FUNCAO
	public void write(OutputStream output) throws IOException {
		// Escrever cabecalho do GIF
		writeGIFHeader(output);

		// Escrever cabecalho do Image Block -> Img Block Header + min code size
		writeImageBlockHeader(output);

        lzwCodification();

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
		minCodeSize = (byte)(numBits(numColors - 1));
		if (minCodeSize == 1) {  // Imagens binarias -> caso especial (pag. 26 do RFC)
			minCodeSize++;
		}

		output.write(minCodeSize);
	}
}
