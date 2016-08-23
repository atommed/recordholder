#define OUT_NAME "cover.jpg"

#include <stdio.h>
#include <stdlib.h>

#include <libavformat/avformat.h>

int main(int argc, char **argv){
	AVOutputFormat *ofmt = NULL;
	AVFormatContext *ifmt_ctx = NULL, *ofmt_ctx = NULL;
        AVStream *in_stream, *out_stream;	
	AVPacket pkt;
	int ret = EXIT_SUCCESS;
	int cover_stream_id;

	if(argc != 2){
		fprintf(stderr,
			"Usage: %s <file_name>\n"
			"Get cover from audio file\n"
			"\n", argv[0]);
		exit(EXIT_FAILURE);
	}

	av_register_all();


	if((ret = avformat_open_input(&ifmt_ctx, argv[1],0,0)) < 0){
		fprintf(stderr, "Could not open input file %s",argv[1]);
		goto end;
	}
	if((ret = avformat_find_stream_info(ifmt_ctx, NULL)) < 0){
		fprintf(stderr, "Can't find stream info");
		goto end;
	}

	//av_dump_format(ifmt_ctx, 0, argv[1], 0);

	avformat_alloc_output_context2(&ofmt_ctx, NULL, NULL, OUT_NAME);
	if(!ofmt_ctx){
		fprintf(stderr, "Could not create output context\n");
		ret = AVERROR_UNKNOWN;
		goto end;
	}
	cover_stream_id = av_find_best_stream(ifmt_ctx,
			                      AVMEDIA_TYPE_VIDEO,
					      -1,-1, NULL, 0);
	if(cover_stream_id < 0){
		fputs("Can't find good cover\n", stderr );
		ret = cover_stream_id;
		goto end;
	}

	in_stream = ifmt_ctx->streams[cover_stream_id];
	out_stream = avformat_new_stream(ofmt_ctx, in_stream->codec->codec);
	if(!out_stream){
		fputs("Can't alloc output stream\n", stderr);
		ret = AVERROR_UNKNOWN;
		goto end;
	}
	ret = avcodec_copy_context(out_stream->codec, in_stream->codec);
	if(ret < 0){
		fputs("Can't copy codec context", stderr);
		goto end;
	}

	ret = avformat_write_header(ofmt_ctx, NULL);
	if(ret < 0){
		fputs("Can't write\n", stderr);
		goto end;
	}

	av_read_frame(ifmt_ctx, &pkt);
	pkt.pos = -1;
	pkt.stream_index = 0;
	pkt.pts = 0;
	pkt.dts = 0;
	pkt.duration = 0;
	av_interleaved_write_frame(ofmt_ctx, &pkt);
	av_packet_unref(&pkt);
	av_write_trailer(ofmt_ctx);

end:
	avformat_close_input(&ifmt_ctx);
	avformat_free_context(ofmt_ctx);
	exit(EXIT_SUCCESS);
}
