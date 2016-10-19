function distribuicaoEstatisticaEntropia(filename)

    [path,name,ext] = fileparts(filename);
    
    % imagem
    if(strcmp(ext,'.bmp'))
        imagem = imread(filename);
        displayHistograma(imagem);
        fprintf('entropia de %s: %d\n', filename, entropia(imagem));
        
    % som
    else if(strcmp(ext,'.wav'))
        [som, freq, nBits ] = getSoundData(filename);
        d = 1/(2^nBits);
        alf = num2cell(-1:d:1);
        displayHistograma(som,alf);
        fprintf('entropia de %s: %d\n', filename, entropia(som,alf));
        
    % texto
    else
        alf = {'a' 'b' 'c' 'd' 'e' 'f' 'g' 'h' 'i' 'j' 'k' 'l' 'm' 'n' 'o' 'p' 'q' 'r' 's' 't' 'u' 'v' 'w' 'x' 'y' 'z'};
        ficheiro = fopen('data/Texto.txt', 'r');
        texto = fscanf(ficheiro,'%c');
        fclose(ficheiro);
        displayHistograma(texto,alf);
        fprintf('entropia de %s: %d\n', filename, entropia(texto,alf));
    end
    
end

