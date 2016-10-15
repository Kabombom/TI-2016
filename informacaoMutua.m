function info = informacaoMutua(query, target, alf, step)

    targetLength = length(target);
    queryLength = length(query);
    limit = targetLength - queryLength + 1;
    
    for i=1:step:limit
        partTarget = target(i:i + length(query) - 1);
        info(i / step) = calcMutualInf(query, partTarget, alf);
    end
    
end

