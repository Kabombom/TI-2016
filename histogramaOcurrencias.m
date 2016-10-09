function graf = histogramaOcurrencias(p, A)
    %TO IMPROVE(Se implementarem mudanças dêem update ao numero das linhas)
    %linha 17: Concatenar todas as colunas de p
    %linha 25: Converter todos os caracteres de todas as linhas de um ficheiro de texto para inteiro (Nao sei como o matlab le ficheiros de texto, nao sei se cada linha do ficheiro de texto fica uma linha no array)
    
    %imagem
    if(nargin==1)
       	graf = p;
        return;
    end
    
    %Verificar se é uma string
    [lines, collumns] = size(p);
    
    %Se for um som ou uma matriz com varias linhas e colunas
    if(lines > 1) 
        %converter matrix numa linha apenas
        p = reshape(p, [1, lines*collumns]);
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
end
