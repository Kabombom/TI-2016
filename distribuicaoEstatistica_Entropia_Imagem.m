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
    alfabeto = (1:256); % alfabeto de uma imagem
    alfabeto = mat2cell(alfabeto,1,ones(1,size(alfabeto,2)));
    graf = histogramaOcurrencias(matrizUnidimensional, alfabeto);
    histogram(graf);
    entropia(matrizUnidimensional, alfabeto)
end