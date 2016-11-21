#include <stdio.h>
#include <stdlib.h>

#include <libavformat/avformat.h>

enum STATUS_CODES {
ST_OK = 0,
ST_NO_COVER_STREAM = 1,
LIBAV_ERROR,
ERR_OPEN_FILE,
ERR_AVCONTEXT_FAIL,
ERR_STREAM_FAIL,
ERR_NO_AUDIO_STREAM,
ERR_BAD_USAGE
};

static int libav_error;


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
	while((tag = av_dict_get(fmt->metadata, 
				"",
				tag,
				AV_DICT_IGNORE_SUFFIX))){
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
		ret = ST_NO_COVER_STREAM;
		goto end;
	}
	in_stream = ifmt->streams[cover_stream_id];
	avformat_alloc_output_context2(&ofmt, NULL, NULL, filename);
	if(!ofmt){
		fputs("Can't create ouptut context\n", stderr);
		ret = ERR_AVCONTEXT_FAIL;
		goto end;
	}
	if((libav_error = avio_open(&ofmt->pb, filename, AVIO_FLAG_WRITE)) < 0){
		fprintf(stderr, "Can't open input file %s\n", filename);
                ret = LIBAV_ERROR;
		goto end;
	}

	if(!(out_stream = avformat_new_stream(ofmt, in_stream->codec->codec))){
		fputs("Can't alloc output stream\n", stderr);
		ret = ERR_STREAM_FAIL;               
		goto end;
	}
	if((libav_error = avcodec_copy_context(out_stream->codec, in_stream->codec))< 0){
		fputs("Can't copy codec context\n", stderr);
                ret = LIBAV_ERROR;
		goto end;
	}
	if((libav_error = avformat_write_header(ofmt, NULL)) < 0){
		fputs("Can't write cover\n", stderr);
                ret = LIBAV_ERROR;
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
	ret = ST_OK;

end:
	if(ofmt)
		avio_closep(&ofmt->pb);
	avformat_free_context(ofmt);
	return ret;
}

/*Prints comma separated list of available extensions*/
void print_available_extensions(AVFormatContext* fmt){
	printf("%s\n", fmt->iformat->extensions);
}

/*Prints length in seconds and bitrate*/
void print_track_info(AVFormatContext* fmt){
	int best_audio_stream_id;
	AVStream* s;
        best_audio_stream_id = av_find_best_stream(fmt, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0);
	s = fmt->streams[best_audio_stream_id];
	printf("%.3f %"PRId64"\n",s->duration * av_q2d(s->time_base),fmt->bit_rate/1000l);
}

int main(int argc, char **argv){
	AVFormatContext *ifmt = NULL;
	char *file_name, *out_cover_name;
	int ret;

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
	if((libav_error = avformat_open_input(&ifmt, file_name, NULL, NULL)) < 0){
		fprintf(stderr, "Can't open input file %s\n", file_name);
                ret = LIBAV_ERROR;
		goto end;
	}
	if((libav_error = avformat_find_stream_info(ifmt, NULL)) < 0){
		fputs("Can't find stream info\n", stderr);
                ret = LIBAV_ERROR;
		goto end;
	}
	if(av_find_best_stream(ifmt, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0) < 0){
		fputs("Can't find audio stream in file\n", stderr);
		ret = ERR_STREAM_FAIL;
		goto end;
	}
	print_available_extensions(ifmt);
	print_track_info(ifmt);
	dump_metadata(ifmt);
	ret = save_cover_art(ifmt, out_cover_name);

end:
	avformat_close_input(&ifmt);
	exit(ret);
}
