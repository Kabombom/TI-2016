function info = calcMutualInf(x, y, alf)
    
    matrix = zeros(length(alf));
    a = floor(- min(min(x), min(y)) + 1);

    for i=1:length(x)
        matrix(x(i) + a, y(i) + a) = matrix(x(i) + a, y(i) + a) + 1;
    end

    sumElements = sum(sum(matrix));
    
    sumCollums = sum(matrix);
    sumLines = sum(transpose(matrix));
    probX = sumCollums ./ sumElements;
    probY = sumLines ./ sumElements;
    probXY = matrix ./ sumElements; 
    
    probX = nonzeros(probX);
    entX = -sum(probX .* log2(probX));
    
    probY = nonzeros(probY);
    entY = -sum(probY .* log2(probY));
    
    probXY = nonzeros(probXY);
    entXY = -sum(probXY .* log2(probXY));

    info = entX + entY - entXY;
    
end