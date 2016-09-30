function histogramaOcurrencias(p, A)
    hist = zeros(length(A), 1);
    for i = 1:length(A)
        hist(i) = length(find(p == i));
    end
    bar(hist);
    set(gca,'ytick',0:10)
end