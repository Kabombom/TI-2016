function distribuicaoEstatistica_Entropia_Imagem(file)
    imagem = imread(file);
    linhas = length(imagem);
    colunas = size(imagem, 2);
    matrizUnidimensional = zeros(1, linhas * colunas); % matriz com todos os pixels da imagem 1 x (linhas*colunas)
    counter = 1; % contador do indice da matriz unidimensional
    for i = 1:linhas
       for j = 1:colunas
           matrizUnidimensional(counter) = imagem(i,j);
           counter = counter + 1;
       end
    end
    alfabeto = num2cell(0:255);
    graf = histogramaOcurrencias(matrizUnidimensional, alfabeto);
    histogram(graf);
    entropia(matrizUnidimensional, alfabeto)
end