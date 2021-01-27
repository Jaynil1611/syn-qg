# pip install fairseq
# pip install moses
# pip install 
# requires "torch", "regex", "requests", "tqdm", "sacremoses", "subword-nmt", fastBPE
import socket
import torch

print("Torch version: " + torch.__version__)

#torch.hub.list('pytorch/fairseq')  # [..., 'transformer.wmt16.en-de', ... ]

print("Starting to load English to German Translation Model:")
en2de = torch.hub.load('pytorch/fairseq','transformer.wmt16.en-de', tokenizer='moses', bpe='subword_nmt')
print(type(en2de)) #transformer.wmt19.en-de	11 gb
# transformer.wmt19.de-en
print("Completed loading English to German Translation Model.\n")
print("Starting to load German to English Translation Model:")
de2en = torch.hub.load('pytorch/fairseq', 'transformer.wmt19.de-en.single_model', tokenizer='moses', bpe='fastbpe')
print("Completed loading German to English Translation Model.\n")

host = '127.0.0.1'        # Symbolic name meaning all available interfaces
port = 12389     # Arbitrary non-privileged port
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host, port))
s.listen(1)
conn, addr = s.accept()
print('Connected by', addr)
while True:
    data = (conn.recv(1024))
    if not data: break
    try:
        en = data.decode()
        print("\nReceived              :"+en)
        de=en2de.translate(en)
        #print("Translated German     : "+de)
        y = de2en.translate(de)
        print("BackTranslated English: "+y)
    except UnicodeDecodeError:
        print("Unicode Decode error")
        y="UnicodeDecodeError" # a very high number
    conn.sendall((y + "\n").encode('utf8'))
conn.close()
