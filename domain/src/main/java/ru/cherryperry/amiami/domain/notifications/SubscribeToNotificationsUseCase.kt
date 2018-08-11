package ru.cherryperry.amiami.domain.notifications

import ru.cherryperry.amiami.domain.CompletableUseCase
import ru.cherryperry.amiami.domain.repository.PushNotificationService
import rx.Completable
import javax.inject.Inject

class SubscribeToNotificationsUseCase @Inject constructor(
    private val pushNotificationService: PushNotificationService
) : CompletableUseCase<Boolean>() {

    override fun run(param: Boolean): Completable = if (param) {
        pushNotificationService.enable()
    } else {
        pushNotificationService.disable()
    }
}
