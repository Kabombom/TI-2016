function graf = histogramaOcurrencias(p, A)    
    %imagem
    if(nargin==1)
       	graf = p;
        return;
    end
    
    [lines, collumns] = size(p);
    
    %Se for um som
    if(lines > 1) 
        %converter matrix numa linha apenas
        p = reshape(p, [1, lines*collumns]);
        %converte celulas em numeros
        convA = cell2mat(A);
        %TO DO
    end
    
    %Se for uma string
    if(ischar(p))
        %converter letras para inteiros
        p = uint16(p);
        %converter alfabeto para inteiros
        convA = cellfun(@uint16, A); 
        %Associa letras em A aos seu ASCCI code em convA
        graf = categorical(p,convA,A);
    end         
end
