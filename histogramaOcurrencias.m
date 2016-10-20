function graf = histogramaOcurrencias(p, A)  

    % imagem
    if(nargin==1)
       	graf = p;
        
    % texto
    elseif(ischar(p))
        % converter letras para inteiros
        p = uint16(p);
        % converter alfabeto para inteiros
        convA = cellfun(@uint16, A); 
        % associa letras em A aos seu ASCCI code em convA
        graf = categorical(p,convA,A);
        
    % som
    else
        [lines, collumns] = size(p);
    
        % converte matriz numa linha apenas
        p = p(:);
        % converte celulas em numeros
        convA = cell2mat(A);
        counter = 1;
        maxConvA = length(convA);
        maxP = length(p);
        graf = p;
    end
    
end
