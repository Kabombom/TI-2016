function info = informacaoMutua(query, target, alf, step)

    targetLength = length(target);
    queryLength = length(query);
    limit = targetLength - queryLength + 1;
    
    info = zeros(1, length(1:step:limit));
    
    for i=1:step:limit
        partTarget = target(i:i + length(query) - 1);
        info(ceil(i / step)) = calcMutualInf(query, partTarget, alf);
    end
    
    
end

