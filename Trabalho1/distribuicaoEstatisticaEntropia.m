function distribuicaoEstatisticaEntropia(filename)

    [path,name,ext] = fileparts(filename);
    
    % imagem
    if(strcmp(ext,'.bmp'))
        imagem = imread(filename);
        displayHistograma(imagem);
        fprintf('entropia de %s: %f\n', filename, entropia(imagem));
        
    % som
    elseif(strcmp(ext,'.wav'))
        [som, freq, nBits ] = getSoundData(filename);
        d = 1/(2^nBits); %devia ser 2 em vez de 1
        alf = num2cell(-1:d:1);
        displayHistograma(som,alf);
        fprintf('entropia de %s: %f\n', filename, entropia(som,alf));
        
    % texto
    else
        alf = cellstr((horzcat('a':'z','A':'Z'))');
        alf = alf';
        ficheiro = fopen(filename, 'r');
        texto = fscanf(ficheiro,'%c');
        fclose(ficheiro);
        displayHistograma(texto,alf);
        fprintf('entropia de %s: %f\n', filename, entropia(texto,alf));
    end
    
end

