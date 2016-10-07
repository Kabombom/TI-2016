function histogramaOcurrencias(p, A)
    %TO IMPROVE(Se implementarem mudanças dêem update ao numero das linhas)
    %linha 10: Adicionar loop para concatenar todas as colunas de p
    %linha 18: Converter todos os caracteres de todas as linhas de um ficheiro de texto para inteiro (Nao sei como o matlab le ficheiros de texto, nao sei se cada linha do ficheiro de texto fica uma linha no array)

    %Verificar se é uma string
    [lines, cols] = size(p);
    
    %Se for um som ou uma matriz com varias linhas e colunas
    if(lines > 1) 
        %juntamos o canal direito à coluna do lado esquerdo
        p = vertcat(p(:,1),p(:,2));
        %Convertemos essa coluna numa linha
        p = transpose(p);
    end
    
    %Se for uma string
    if(ischar(p))
        %converter letras para inteiros
        p = uint16(p);
        %converter alfabeto para inteiros
        convA = cellfun(@uint16, A);  
    %Se for um array de numeros    
    else
        %converte celulas em numeros
        convA = cell2mat(A);
        %converte numeros em strings
        A = cellfun(@num2str, A, 'UniformOutput', false);       
    end
           
    %Associa alfabeto dado aos itens na matriz, sendo apenas apresentados
    %elementos do alfabeto
    graf = categorical(p,convA,A);
    histogram(graf);
    
end
