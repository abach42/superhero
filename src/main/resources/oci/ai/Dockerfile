FROM ollama/ollama:0.16.3
RUN ollama serve & sleep 5 && \
    ollama pull mxbai-embed-large && \
    ollama pull mistral