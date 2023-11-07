package com.victor_sml.playlistmaker.sharing.domain

import com.victor_sml.playlistmaker.sharing.domain.api.ExternalNavigator
import com.victor_sml.playlistmaker.sharing.domain.api.SharingInteractor
import com.victor_sml.playlistmaker.sharing.domain.api.SharingRepository
import com.victor_sml.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val sharingRepository: SharingRepository
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareText(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String = sharingRepository.getShareAppLink()

    private fun getSupportEmailData(): EmailData = sharingRepository.getSupportEmailData()

    private fun getTermsLink(): String = sharingRepository.getTermsLink()
}