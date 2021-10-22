FROM ipfs/go-ipfs:v0.10.0 as builder
COPY start_ipfs.patch /
RUN cd /usr/local/bin && patch -p1 < /start_ipfs.patch

FROM ipfs/go-ipfs:v0.10.0
COPY --from=builder /usr/local/bin/start_ipfs /usr/local/bin/
