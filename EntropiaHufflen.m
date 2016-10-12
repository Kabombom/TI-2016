function EntropiaHufflen()

    % Lena.bmp
    imagem = imread('data/Lena.bmp');
    imagem = imagem(:);
    c = imhist(imagem);
    c = transpose(c);
    huf = hufflen(c);
    ent = sum((c/sum(c)) .* huf);
    s = sprintf('entropia de Lena.bmp %d\n', ent);
    disp(s);
    
    % CT1.bmp
    imagem = imread('data/CT1.bmp');
    c = imhist(imagem);
    c = transpose(c);
    huf = hufflen(c);
    ent = sum((c/sum(c)) .* huf);
    s = sprintf('entropia de CT1.bmp %d\n', ent);
    disp(s);
    
    % Binaria.bmp
    imagem = imread('data/Binaria.bmp');
    c = imhist(imagem);
    c = transpose(c);
    huf = hufflen(c);
    ent = sum((c/sum(c)) .* huf);
    s = sprintf('entropia de Binaria.bmp %d\n', ent);
    disp(s);
    
    % saxriff.wav
    [som, freq, nBits ] = getSoundData('data/saxriff.wav');
    d = 1/(2^nBits);
    alf = num2cell(-1:d:1);
    [lines, collumns] = size(som);
    if(lines > 1) 
        %converter matrix numa linha apenas
        som = reshape(som, [1, lines*collumns]);
        %converte celulas em numeros
        convA = cell2mat(alf);

        graf = zeros(1,lines*collumns); 
        counter = 1;
        maxConvA = length(convA);
        maxP = length(som);
        for i=1:maxConvA
            for j=1:maxP
                if(convA(i)==som(j))
                   graf(counter) = som(j);
                   counter = counter + 1;
                end
            end
        end
    end
    counts = histcounts(graf);
    frequencias = counts(1,:);
    HLen = hufflen(frequencias);
    ent = sum((frequencias/sum(frequencias)) .* HLen);
    s = sprintf('entropia de saxriff.wav %d\n', ent);
    disp(s)
    
    % Texto.txt
    letters = {'a' 'b' 'c' 'd' 'e' 'f' 'g' 'h' 'i' 'j' 'k' 'l' 'm' 'n' 'o' 'p' 'q' 'r' 's' 't' 'u' 'v' 'w' 'x' 'y' 'z'};
    ficheiro = fopen('data/Texto.txt');
    texto = fscanf(ficheiro, '%c');
    fclose(ficheiro);
    graf = histogramaOcurrencias(texto , letters);
    counts = histcounts(graf);
    frequencias = counts(1,:);
    HLen = hufflen(frequencias);
    ent = sum((frequencias/sum(frequencias)) .* HLen);
    s = sprintf('entropia de Texto.txt %d\n', ent);
    disp(s)
    
end
