import java.io.*;
import java.awt.*;
import java.awt.image.*;

public class MyGIFEncoder 
{
	short width, height; //largura e altura da imagem
	int numColors; //n�mero de cores distintas na imagem
	byte pixels[]; //array com os �ndices de cores, i.e., array com a imagem indexada
	byte colors[]; //array 3 vezes maior que o anterior com os n�veis RGB da imagem 
		//associados a cada �ndice (cores a escrever na Global Color Table)
	byte [][] r, g, b; //matrizes com os valores R,G e B em cada c�lula da imagem
	byte minCodeSize; //tamanho m�nimo dos c�digos LZW
	
	
	//----- construtor e fun��es auxiliares (para obten��o da imagem indexada)
	public MyGIFEncoder(Image image) throws InterruptedException, AWTException 
	{
		width = (short)image.getWidth(null);
		height = (short)image.getHeight(null);
		
		//definir a iomagem indexada
		getIndexedImage(image);
	}
	
	
	//convers�o de um objecto do tipo Image numa imagem indexada
	private void getIndexedImage(Image image) throws InterruptedException, AWTException
	{
		//matriz values: cada entrada conter� um inteiro com 4 conjuntos de 8 bits, 
		//pela seguinte ordem: alpha, red, green, blue
		//obtidos com recurso ao m�todo grabPixels da classe PixelGrabber
		int values[] = new int[width * height];  
		PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, values, 0, width);		
		grabber.grabPixels();		
		
		//obter imagem RGB
		getRGB(values);
		
		//converter para imagem indexada
		RGB2Indexed();
	}
	
	
	//obten��o dos valores RGB a partir dos valores lidos pelo PixelGrabber no m�todo anterior
	private void getRGB(int [] values) throws AWTException
	{
		r = new byte[width][height];
		g = new byte[width][height];
		b = new byte[width][height];
		
		int index = 0;
		
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) 
			{
				r[x][y] = (byte)((values[index] >> 16) & 0xFF);
				g[x][y] = (byte)((values[index] >> 8) & 0xFF);
				b[x][y] = (byte)((values[index]) & 0xFF);  
				index++;
			}
	}
	
	
	//convers�o de matriz RGB para indexada: m�ximo de 256 cores
	private void RGB2Indexed() throws AWTException 
	{
		pixels = new byte[width * height];
		colors = new byte[256 * 3];
		int colorNum = 0;
		
		for (int x = 0; x < width; x++) 
		{
			for (int y = 0; y < height; y++) 
			{
				int index;
				for (index = 0; index < colorNum; index++)
					if (colors[index * 3] == r[x][y] && 
						colors[index * 3 + 1] == g[x][y] &&
						colors[index * 3 + 2] == b[x][y])
						break;
		
				if (index > 255)
				{
					System.out.println("Demasiadas cores...");
					System.exit(-1);
				}

				pixels[y * width + x] = (byte)index;
		
				if (index == colorNum) 
				{
					colors[index * 3] = r[x][y];
					colors[index * 3 + 1] = g[x][y];
					colors[index * 3 + 2] = b[x][y];
					colorNum++;
				}
			}
		}
		
		//define o n�mero de cores como pot�ncia de 2 (devido aos requistos da Global Color Table)
		numColors = nextPower2(colorNum);
		
		//refine o array de cores com base no n�mero final obtido
		byte copy[] = new byte[numColors * 3];
		System.arraycopy(colors, 0, copy, 0, numColors * 3);
		colors = copy;
	}
	
		
	//determina��o da pr�xima pot�ncia de 2 de um dado inteiro n
	private int nextPower2(int n) 
	{
		int ret = 1, nIni = n;
		
		if (n == 0)
			return 0;
		
		while (n != 0)
		{
			ret *= 2;
			n /= 2;
		}
		
		if (ret % nIni == 0)
			ret = nIni;
		
		return ret;
	}
	
	
	//n�mero de bits necess�rio para representar n
	private byte numBits(int n)
	{
		byte nb = 0;
		
		if (n == 0)
			return 0;
		
		while (n != 0)
		{
			nb++;
			n /= 2;
		}
		
		return nb;
	}


//---------------------------------------------------------------------------------
//---------------------------------------------------------------------------------
//---------------------------------------------------------------------------------

	//---- Fun��o para escrever imagem no formato GIF, vers�o 87a
	//// COMPLETAR ESTA FUN��O
	public void write(OutputStream output) throws IOException 
	{
		//Escrever cabe�alho do GIF
		writeGIFHeader(output);
		
		//Escrever cabe�alho do Image Block
		writeImageBlockHeader(output);
		
		/////////////////////////////////////////
		//Escrever blocos com 256 bytes no m�ximo
		/////////////////////////////////////////
		//CODIFICADOR LZW AQUI !!!! 
		//escrever blocos comprimidos, com base na matriz pixels e no minCodeSize;
		//       (o primeiro bloco tem, depois do block size, o clear code)
		//escrever end of information depois de todos os blocos
		//escrever block terminator (0x00)
		//
		//
		
		
		//trailer
		byte trailer = 0x3b;
		output.write(trailer);
		
		//flush do ficheiro (BufferedOutputStream utilizado)
		output.flush();
	}
	
	
	//--------------------------------------------------
	//gravar cabe�alho do GIF (at� global color table)
	private void writeGIFHeader(OutputStream output) throws IOException
	{
		//Assinatura e vers�o (GIF87a)
		String s = "GIF87a";
		for (int i = 0; i < s.length(); i++)
			output.write((byte)(s.charAt(i)));	
	
		//Ecr� l�gico (igual � da dimens�o da imagem) --> primeiro o LSB e depois o MSB
		output.write((byte)(width & 0xFF));
		output.write((byte)((width >> 8) & 0xFF));
		output.write((byte)(height & 0xFF));
		output.write((byte)((height >> 8) & 0xFF));
		
		//GCTF, Color Res, SF, size of GCT
		byte toWrite, GCTF, colorRes, SF, sz;
		GCTF = 1;
		colorRes = 7;  //n�mero de bits por cor prim�ria (-1)
		SF = 0;
		sz = (byte) (numBits(numColors - 1) - 1); //-1: 0 --> 2^1, 7 --> 2^8
		toWrite = (byte) (GCTF << 7 | colorRes << 4 | SF << 3 | sz);
		output.write(toWrite);
	
		//Background color index
		byte bgci = 0;
		output.write(bgci);
	
		//Pixel aspect ratio
		byte par = 0; // 0 --> informa��o sobre aspect ratio n�o fornecida --> decoder usa valores por omiss�o
		output.write(par);
	
		//Global color table
		output.write(colors, 0, colors.length);
	}
	
	
	//--------------------------------------------------------
	//gravar cabe�alho do Image Block (LZW minimum code size)
	private void writeImageBlockHeader(OutputStream output) throws IOException
	{
		//Image separator
		byte imSep = 0x2c;
		output.write(imSep);
		
		//Image left, top, width e height
		byte left = 0, top = 0;
		output.write((byte)(left & 0xFF));
		output.write((byte)((left >> 8) & 0xFF));
		output.write((byte)(top & 0xFF));
		output.write((byte)((top >> 8) & 0xFF));
		output.write((byte)(width & 0xFF));
		output.write((byte)((width >> 8) & 0xFF));
		output.write((byte)(height & 0xFF));
		output.write((byte)((height >> 8) & 0xFF));
		
		//LCTF, Interlace, SF, reserved, size of LCT
		byte toWrite, LCTF, IF, res, SF, sz;
		LCTF = 0;IF = 0;	
		SF = 0;
		res = 0;
		sz = 0; 
		toWrite = (byte) (LCTF << 7 | IF << 6 | SF << 5 | res << 4 | sz);
		output.write(toWrite);
		
		//Local Color Table: n�o definida
		
		//LZW Minimum Code Size (com base no n�mero de cores utilizadas)
		minCodeSize = (byte)(numBits(numColors - 1));
		if (minCodeSize == 1)  //imagens bin�rias --> caso especial (p�g. 26 do RFC)
			minCodeSize++;

		output.write(minCodeSize);
	}
}