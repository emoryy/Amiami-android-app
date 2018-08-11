package ru.cherryperry.amiami.domain.repository

import ru.cherryperry.amiami.domain.model.HighlightRule
import rx.Completable
import rx.Single
import java.io.InputStream
import java.io.OutputStream

interface HighlightExportRepository {

    /**
     * Write list of [HighlightRule] to [OutputStream].
     * [OutputStream] would be closed.
     */
    fun export(rules: List<HighlightRule>, outputStream: OutputStream): Completable

    /**
     * Read list of [HighlightRule] from [InputStream].
     * [InputStream] would be closed.
     */
    fun import(inputStream: InputStream): Single<List<HighlightRule>>
}
