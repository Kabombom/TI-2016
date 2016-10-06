function histogramaOcurrencias(p, A)
    %Verificar se é uma string
    
    [lines, cols] = size(p);
    
    %Se for um som
    if(lines > 1) 
        p = vertcat(p(:,1),p(:,2));
        p = transpose(p);
    end
    
    %Se for alfabeto
    if(ischar(p))
        %converter letras para inteiros
        p = uint16(p);
        
        %converter alfabeto para inteiros
        convA = cellfun(@uint16, A);  
    %Se for matrix de numeros    
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