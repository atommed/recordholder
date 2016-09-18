#include <stdio.h>
#include <stdlib.h>

#include <libavformat/avformat.h>

#define ERR_NO_STREAM (3)
#define ERR_BAD_USAGE (2)
#define OK_NO_COVER_STREAM (1)


/**
 *Escape format:
 * newline => \n
 * \ => \\
 * = => \=
 */
void print_escaped(char* s){
	while(*s){
		switch(*s){
			case '\n':
				putchar('\\');
				putchar('n');
				break;
			case '\\': 
				putchar('\\');
				putchar('\\');
				break;
			case '=' : 
				putchar('\\');
				putchar('=');
				break;
			default: putchar(*s);break;
		}
		s++;
	}
}

void print_metadata_entry(AVDictionaryEntry *tag){
	print_escaped(tag->key);
	putchar('=');
	print_escaped(tag->value);
	putchar('\n');
}

void dump_metadata(AVFormatContext* fmt){
	AVDictionaryEntry *tag = NULL;
	while(tag = av_dict_get(fmt->metadata, 
				"",
				tag,
				AV_DICT_IGNORE_SUFFIX)){
		print_metadata_entry(tag);
	}		
}

int save_cover_art(AVFormatContext* ifmt,char *filename){
	int cover_stream_id,ret;
	AVFormatContext *ofmt = NULL;
	AVStream *in_stream, *out_stream;
	AVPacket pkt;

	if((cover_stream_id = av_find_best_stream(ifmt,
					AVMEDIA_TYPE_VIDEO,
					-1,-1,NULL, 0)) < 0){
		fputs("Can't find cover stream\n", stderr);
		ret = OK_NO_COVER_STREAM;
		goto end;
	}
	in_stream = ifmt->streams[cover_stream_id];
	avformat_alloc_output_context2(&ofmt, NULL, NULL, filename);
	if(!ofmt){
		fputs("Can't create ouptut context\n", stderr);
		ret = AVERROR_UNKNOWN;
		goto end;
	}
	if((ret = avio_open(&ofmt->pb, filename, AVIO_FLAG_WRITE)) < 0){
		fprintf(stderr, "Can't open input file %s\n", filename);
		goto end;
	}

	if(!(out_stream = avformat_new_stream(ofmt, in_stream->codec->codec))){
		fputs("Can't alloc output stream\n", stderr);
		ret = AVERROR_UNKNOWN;
		goto end;
	}
	if((ret = avcodec_copy_context(out_stream->codec, in_stream->codec))< 0){
		fputs("Can't copy codec context\n", stderr);
		goto end;
	}
	if((ret = avformat_write_header(ofmt, NULL)) < 0){
		fputs("Can't write cover\n", stderr);
		goto end;
	}

	av_read_frame(ifmt, &pkt);
	pkt.pos = -1;
	pkt.stream_index = out_stream->index;
	pkt.pts = 0;
	pkt.dts = 0;
	pkt.duration = 0;
	av_interleaved_write_frame(ofmt, &pkt);
	av_packet_unref(&pkt);
	av_write_trailer(ofmt);
	ret = 0;

end:
	if(ofmt)
		avio_closep(&ofmt->pb);
	avformat_free_context(ofmt);
	return ret;
}

int main(int argc, char **argv){
	AVFormatContext *ifmt = NULL;
	char *file_name, *out_cover_name;
	int ret = EXIT_SUCCESS;

	/*Check input args*/
	if(argc != 3){
		fprintf(stderr,
				"Usage: %s <file name> <out_cover_name>\n"
				"Get metadata and extract album art from audio\n"
				"\n", argv[0]);
		exit(ERR_BAD_USAGE);		
	}
	file_name = argv[1];
	out_cover_name = argv[2];
	av_register_all();

	/*Create context*/
	if((ret = avformat_open_input(&ifmt, file_name, NULL, NULL)) < 0){
		fprintf(stderr, "Can't open input file %s\n", file_name);
		goto end;
	}
	if((ret = avformat_find_stream_info(ifmt, NULL)) < 0){
		fputs("Can't find stream info\n", stderr);
		goto end;
	}
	if(av_find_best_stream(ifmt, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0) < 0){
		fputs("Can't find audio stream in file\n", stderr);
		ret = ERR_NO_STREAM;
		goto end;
	}

	dump_metadata(ifmt);
	ret = save_cover_art(ifmt, out_cover_name);

end:
	avformat_close_input(&ifmt);
	exit(ret);
}
