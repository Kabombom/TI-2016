function displayHistograma( graf )
    if(iscategorical(graf))
       histogram(graf); 
    else
        imhist(graf);
    end
end

