function distribuicaoEstatistica_Entropia(filename)
    [path,name,ext] = fileparts(filename);
    %Se for uma imagem
    if(strcmp(ext,'.bmp'))
        imagem = imread(filename);
        displayHistograma(imagem);
        disp(sprintf('entropia: %d',entropia(imagem)));
    %Se for um fiecheiro de som
    else if(strcmp(ext,'.wav'))
        [som, freq, nBits ] = getSoundData(filename);
        d = 1/(2^nBits);
        alf = num2cell(-1:d:1);
        displayHistograma(som,alf);
        disp(sprintf('entropia: %d',entropia(som,alf)));
    %Se for ficheiro de texto
    else
        alf = {'a' 'b' 'c' 'd' 'e' 'f' 'g' 'h' 'i' 'j' 'k' 'l' 'm' 'n' 'o' 'p' 'q' 'r' 's' 't' 'u' 'v' 'w' 'x' 'y' 'z'};
        ficheiro = fopen('data/Texto.txt', 'r');
        texto = fscanf(ficheiro,'%c');
        fclose(ficheiro);
        displayHistograma(texto,alf);
        disp(sprintf('entropia: %d',entropia(texto,alf)));
    end
end

