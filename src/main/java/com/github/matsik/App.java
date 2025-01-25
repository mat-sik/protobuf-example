package com.github.matsik;

import com.github.matsik.netmsg.GetFileRequest;
import com.github.matsik.netmsg.MessageWrapper;
import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class App {

    private static final int BUFFER_SIZE = 1024;

    // to generate the sources run:
    // protoc \
    // --proto_path=. \
    // --java_out=target/generated-sources \
    // netmsg.proto
    public static void main(String[] args) throws IOException {
        GetFileRequest request = GetFileRequest.newBuilder()
                .setFileName("foo")
                .build();

        MessageWrapper wrapper = MessageWrapper.newBuilder()
                .setGetFileRequest(request)
                .build();

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        CodedOutputStream output = CodedOutputStream.newInstance(buffer);

        wrapper.writeTo(output);
        output.flush();

        buffer.flip();

        try (FileChannel channel = FileChannel.open(
                Paths.get("foo.txt"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
        ) {
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
        }
    }
}
