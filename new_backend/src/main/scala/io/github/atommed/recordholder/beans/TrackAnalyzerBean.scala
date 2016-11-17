package io.github.atommed.recordholder.beans

import java.io.File
import java.nio.file.{Files, Path, Paths}
import javax.annotation.{PostConstruct, Resource}
import javax.ejb.Stateless
import javax.inject.Inject

import io.github.atommed.recordholder.util.MetadataRetriever


/**
  * Created by gregory on 17.11.16.
  */

@Stateless
class TrackAnalyzerBean {
  @Resource(lookup="java:/recordholder/config/paths/analyzer-executable")
  private var executablePath: String = _
  private var analyzer: MetadataRetriever = _
  private var workDir: Path = _

  def getMetaData(track: File) = analyzer.extractMetadata(track)

  @PostConstruct
  private def init() = {
    workDir = Files.createTempDirectory("metadata_analyzer-wd")
    analyzer = new MetadataRetriever(Paths.get(executablePath))
  }
}
