Introduction 

In this assignment you will do some basic natural language processing (NLP). You will write a program which will construct a language model (for several different languages) and, when asked to classify a text, should select the language whose model is closer to the text in question. The language model (described below) will be constructed from text files. You will be required to achieve this task without using any loops (use streams and lambdas instead). You also need to read the files via multithreading and update the model concurrently.

Folder Structure 

Your program will read a local folder. This local folder will be given as a command line argument. The folder will have the following structure: 

•	Local Folder 

    o	Mystery.txt
    o	Lang1 (e.g. al)
        .	file1-1.txt
        .	file1-2.txt
        .	…
    o	Lang2 (e.g. fr)
        .	file2-1.txt
        .	file2-2.txt
        :
    o	LangK (e.g. en)
        .	filek-1.txt
        .	filek-2.txt

The local folder will have one single text file mystery.txt and a number of subfolders (at least 2), one for each language. You can expect subfolders to be named with two latter code (al – Albanian, en – English, it – Italian, de – German etc.) From each language subfolder, you should only read files that end in .txt suffix and ignore any other files or subfolders.
N-gram model 

When constructing the language model, you will use a model called n-gram. An n-gram in NLP is a sequence of n linguistic items. Such items can be words, sentences, or other components, but for this example you will use letters in a word. Usual values of n in n-gram models when applied to NLP are n=1 (unigram), n=2 (bigrams) or n=3 (trigrams). Unigram model is the simplest, for every word 𝑤 of length 𝑛 
𝑤=𝑤0𝑤1…𝑤𝑛−1, the unigram sequence is simply its individual letters 𝑤0, 𝑤1, …, 𝑤𝑛−1. As an example apple produces the unigram sequence a, p, p, l, e. Bigram model considers pairs of adjacent letters in a word, i.e. 𝑤0𝑤1, 𝑤1𝑤2, …, 𝑤𝑛−2𝑤𝑛−1. The sample apple example produces the bigram sequence ap, pp, pl, le. Similarly the trigram model considers triples of adjacent letters in a word, i.e. 𝑤0𝑤1𝑤2, 𝑤1𝑤2𝑤3, … 𝑤𝑛−3𝑤𝑛−2𝑤𝑛−1. Word apple then produces the trigram sequence app, ppl, ple. Your program will read the value of 
Your language model will then read value n from the command line. You should assume a default value n=2 if it is not supplied.
Document Distance 

Document distance (also known as cosine similarity) quantifies how similar two texts (more precisely their n-gram frequency distributions) are to one another. Without loss of generality we can treat the unigram frequency distribution as a vector 𝐴= [𝑎1,𝑎2,…𝑎𝑛], where each 𝑎𝑖 represents the frequency that the term 𝑖 appears in the language model. For example 𝑎𝑡ℎ= 978 must be interpreted as the fact that the bigram th appears 978 times in the texts of the language. 
Then given two such vectors (i.e. frequency distributions) 𝐴 and 𝐵, their similarity 𝑆 is computed as 
𝑆 = 𝐴∙B/𝐴‖∙‖𝐵‖ = Σ ni=1 ai bi / (√Σ ni=1 ai2 . √ Σ ni=1 bi2 )
This measure falls in the interval [−1,1], where value 1 indicates “exactly the same”, value -1 indicates “exactly the opposite”, and value 0 indicates orthogonality. We can visualize 𝐴 and 𝐵 as vectors in some hyperspace, then 𝑆 is the cosine of the angle 𝛼 between these two vectors. 
𝑆=cos (𝛼) 
𝛼=𝑐𝑜𝑠−1(𝑆) 
When 𝐴 and 𝐵 represent the same document, they are equal to one another, which means their angle is 0 (remember cos(0) = 1). The further apart the vectors are from one another, the larger the angle, and its cosine value moves away from 1.
Solution
Steps followed:

1.	Find n-gram models of each .txt file in a language subfolder.
2.	Merge all models in each language and store that model against the language name.
3.	Form n-gram model for mystery text.
4.	Find document distance and the angle for each language with mystery text and find the minimum among all angles.
5.	Output the language with the minimum distance.
Result

For say the following entries:

. Local folder

    o	mystery.txt
            Aaa   
    o	en
        .	en-1.txt
            aaa    
    o	al
        .	al-1.txt
            bbb

The predicted language for the mystery text will be: en

Conclusion

We can finally wrap the whole text classification problem. Whenever presented with the necessary data (local folder, all the language folder, all the files in each folder, the text file to be classified and the n-gram dimension value), your program should construct a model for every language and the mystery text to be classified. Compute the similarity between the mystery text and each language and select the language with the highest similarity (equivalently smallest angle distance).


