function [y,fs,nBits] = getSoundData( soundName )
       [y,fs] = audioread(soundName);
       info = audioinfo(soundName);
       nBits = info.BitsPerSample;
end

